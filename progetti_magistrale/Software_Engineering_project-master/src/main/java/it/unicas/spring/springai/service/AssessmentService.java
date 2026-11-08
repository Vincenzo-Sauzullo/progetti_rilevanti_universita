package it.unicas.spring.springai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicas.spring.springai.dto.ExplainableFinding;
import it.unicas.spring.springai.dto.ExplainableFindingDraft;
import it.unicas.spring.springai.dto.ExplainableSource;
import it.unicas.spring.springai.model.AssessmentResult;
import it.unicas.spring.springai.model.Macchinario;
import it.unicas.spring.springai.repository.AssessmentResultRepository;
import it.unicas.spring.springai.repository.MacchinarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Service principale per generazione e consultazione degli assessment di conformità.
 * Ruolo architetturale: orchestra retrieval normativo, chiamata LLM, parsing explainable,
 * fallback legacy e persistenza del risultato.
 * Interagisce con `MacchinarioController`, `AssessmentController`, `RagService`,
 * `AssessmentResultRepository` e `MacchinarioRepository`.
 */
public class AssessmentService {

    /*
     * - Questo e' il service principale che crea un assessment di conformita'.
     * - Flusso base: prende il macchinario -> cerca norme/chunk con RAG -> chiede analisi all'AI -> salva su DB.
     * - Prima tenta il formato "explainable" (con citazioni chunkId), se fallisce usa il piano B legacy.
     * - Quindi non si blocca al primo errore: ha un fallback per consegnare comunque un risultato.
     */
    /*
     * Cheat sheet super rapido (metodi, in ordine logico):
     *
     * Entrypoint:
     * - `generateAssessment(Long)`: parte da ID macchina e delega.
     * - `generateAssessment(Macchinario)`: pipeline completa RAG + AI + parse + save.
     *
     * Costruzione input AI:
     * - `buildRagQuery`: crea query semantica normativa.
     * - `buildAssessmentPrompt`: prompt legacy testuale.
     * - `buildExplainableAssessmentPrompt`: prompt JSON con chunkId.
     * - `buildExplainabilityContext`: serializza chunk nel prompt.
     *
     * Parsing output AI:
     * - `parseExplainableAssessmentResponse`: parse JSON explainable e mapping in entity.
     * - `parseAssessmentResponse`: parse fallback legacy markdown.
     * - `parseFindingDrafts`: parse array finding grezzi.
     * - `parseChunkIds`: parse lista id chunk.
     * - `intValue` / `textValue`: helper parse JsonNode.
     *
     * Mappatura chunk/fonti:
     * - `toRetrievedChunks`: normalizza documenti recuperati.
     * - `toExplainableFindings`: risolve chunkIds in fonti concrete.
     * - `toExplainableSource`: converte chunk in fonte serializzabile.
     * - `buildReference`: crea riferimento umano (file + art/par/comma + pagina).
     * - `firstMatch`: helper regex su testo chunk.
     * - `parseInteger` / `rankBasedConfidence`: helper metadata e score fallback.
     *
     * Robustezza/fallback:
     * - `extractFirstJsonObject`: prova a salvare solo la parte JSON.
     * - `safe`: evita null/blank nei campi stringa.
     * - `normalizeRiskLevel`: riduce il rischio a BASSO/MEDIO/ALTO/CRITICO.
     * - `normalizeScore`: clamp 0..100 con default 50.
     * - `extractSection`: estrae sezioni dal formato legacy.
     *
     * Query di consultazione:
     * - `getAssessmentsByMacchinario`: storico assessment macchina.
     * - `getAssessment`: dettaglio assessment per id.
     * - `getAllAssessments`: lista globale assessment.
     *
     * Frase pronta:
     * "AssessmentService e' l'orchestratore: unisce retrieval normativo, ragionamento AI e persistenza finale."
     */
    /*
     * Mappa completa della pipeline assessment (visione ad alto dettaglio):
     *
     * Fase A - recupero input dominio:
     * - entrypoint con ID oppure con entity `Macchinario` gia' caricata.
     * - se macchina non trovata: eccezione immediata.
     *
     * Fase B - retrieval normativo (RAG):
     * - genera query semantica a partire dai dati macchina.
     * - recupera chunk normativi piu' pertinenti.
     * - converte chunk in struttura interna con metadati uniformi.
     *
     * Fase C - tentativo explainable (principale):
     * - costruisce prompt JSON-first con regole severe.
     * - LLM deve restituire finding + raccomandazioni + `chunkIds`.
     * - parsing JSON robusto con normalizzazione rischi/punteggio.
     * - risoluzione `chunkIds` -> fonti concrete (`ExplainableSource`).
     *
     * Fase D - fallback legacy (secondario):
     * - se parse explainable fallisce, usa prompt testuale storico.
     * - parse sezioni markdown (riepilogo, non conformita', raccomandazioni, rischio, score).
     * - mantiene comunque un output utilizzabile e persistibile.
     *
     * Fase E - persistenza:
     * - salva sempre un `AssessmentResult` finale (explainable o legacy).
     * - registra in `documentiUtilizzati` le fonti recuperate/citate.
     *
     * Fase F - consultazione:
     * - metodi di lettura per storico macchina, dettaglio per ID, elenco globale.
     *
     * Principio chiave:
     * - preferenza a spiegabilita' e tracciabilita' senza bloccare l'utente in caso di output imperfetto.
     */
    /*
     * Glossario operativo rapido (campo -> significato concreto):
     * - `riepilogoConformita`: sintesi narrativa dello stato macchina.
     * - `nonConformitaRilevate`: lista problemi identificati.
     * - `raccomandazioni`: lista azioni correttive suggerite.
     * - `livelloRischio`: severita' qualitativa normalizzata.
     * - `punteggioConformita`: severita' quantitativa 0..100.
     * - `documentiUtilizzati`: tracciamento fonti documentali usate.
     *
     * Mini-checklist di debug quando qualcosa non torna:
     * - controlla che il retriever abbia restituito chunk non vuoti.
     * - controlla che `chunkIds` ritornati dal modello esistano davvero.
     * - controlla che il JSON sia ben formato (graffe chiuse, array validi).
     * - controlla la normalizzazione rischio/punteggio prima del save.
     * - controlla eventuale attivazione fallback legacy nei log warning.
     */

    private final ChatClient.Builder chatClientBuilder;
    private final RagService ragService;
    private final MacchinarioRepository macchinarioRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final ObjectMapper objectMapper;

    // Prompt fallback legacy (testuale, non explainable JSON).
    private static final String ASSESSMENT_SYSTEM_PROMPT = """
            Sei un esperto di sicurezza industriale e conformità normativa specializzato nel Regolamento Macchine (UE) 2023/1230 e nelle normative correlate.
            
            Il tuo compito è analizzare le informazioni fornite su un macchinario e produrre un assessment dettagliato di conformità basandoti sui documenti di riferimento forniti.
            
            L'assessment deve includere:
            1. **Riepilogo Conformità**: Una sintesi dello stato di conformità del macchinario
            2. **Non Conformità Rilevate**: Elenco dettagliato delle potenziali non conformità identificate
            3. **Raccomandazioni**: Azioni correttive suggerite per ciascuna non conformità
            4. **Livello di Rischio**: Valutazione del livello di rischio (BASSO, MEDIO, ALTO, CRITICO)
            5. **Punteggio di Conformità**: Un punteggio da 0 a 100
            
            Rispondi sempre in italiano e sii preciso e professionale nelle tue valutazioni.
            """;

    // Prompt explainable: forza citazioni chunkId e output JSON.
    private static final String ASSESSMENT_SYSTEM_PROMPT_EXPLAINABLE = """
            Sei un esperto di sicurezza industriale e conformità normativa specializzato nel Regolamento Macchine (UE) 2023/1230 e nelle normative correlate.

            In ambito normativo l'AI non può essere una "Black Box": ogni segnalazione deve essere verificabile.

            Regole obbligatorie:
            - Usa SOLO i chunk normativi forniti nella sezione "CHUNKS NORMATIVI RECUPERATI (RAG)".
            - Per ogni "Non Conformità" e "Raccomandazione", indica i chunkId che supportano l'affermazione.
            - Se una segnalazione NON è supportata dai chunk disponibili, sii prudente e lascia chunkIds vuoto.
            - Rispondi SOLO con JSON valido, senza testo aggiuntivo e senza markdown.
            """;

    // Numero chunk recuperati quando voglio massima explainability.
    private static final int EXPLAINABILITY_TOP_K = 10;

    // Regex utili per estrarre riferimenti normativi dal testo chunk.
    private static final Pattern ARTICLE_PATTERN = Pattern.compile("(?i)\\b(?:articolo|art\\.)\\s*(\\d+[a-z]?)\\b");
    private static final Pattern COMMA_PATTERN = Pattern.compile("(?i)\\bcomma\\s*(\\d+)\\b");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("(?i)\\bparagrafo\\s*(\\d+)\\b");

    /**
     * Record interno che modella un chunk normativo recuperato dal retriever.
     * Ruolo architetturale: mantiene insieme metadati e testo per la fase explainability.
     *
     * @param chunkId identificativo progressivo locale nel contesto corrente.
     * @param fileName nome documento sorgente.
     * @param page pagina sorgente.
     * @param text contenuto chunk.
     * @param confidence pertinenza chunk.
     */
    private record RetrievedChunk(
            int chunkId,
            String fileName,
            Integer page,
            String text,
            Double confidence
    ) {
    }

    /**
     * Cosa fa: recupera il macchinario per ID e delega la generazione assessment.
     * Perché esiste: offre API service comoda quando il chiamante possiede solo l'identificativo.
     * Chi lo chiama: `MacchinarioController.generateAssessment -> AssessmentService.generateAssessment(Long)`.
     * Input/Output: input ID macchinario; output `AssessmentResult` salvato.
     * Eccezioni/edge case: ID inesistente => `RuntimeException`.
     * Side effects: lettura DB + chiamata a overload generativa.
     *
     * @param macchinarioId ID del macchinario da analizzare.
     * @return assessment persistito.
     */
    @Transactional
    public AssessmentResult generateAssessment(Long macchinarioId) {
        // In pratica: parto dall'ID, recupero la macchina e poi lancio la pipeline completa.
        log.info("Generating assessment for macchinario ID: {}", macchinarioId);

        Macchinario macchinario = macchinarioRepository.findById(macchinarioId)
                .orElseThrow(() -> new RuntimeException("Macchinario non trovato con ID: " + macchinarioId));

        return generateAssessment(macchinario);
    }

    /**
     * Cosa fa: genera assessment explainable/legacy a partire da un macchinario già caricato.
     * Perché esiste: incapsula l'intera pipeline AI e persistenza in una transazione unica.
     * Chi lo chiama: `AssessmentService.generateAssessment(Long)` (diretto).
     * Input/Output: input `Macchinario`; output `AssessmentResult` completo e salvato.
     * Eccezioni/edge case: parse explainable fallito => fallback legacy; errori AI/DB propagati runtime.
     * Side effects: query vector store, chiamate LLM, serializzazione JSON, salvataggio repository.
     *
     * @param macchinario entità macchina da valutare.
     * @return assessment finale persistito.
     */
    @Transactional
    public AssessmentResult generateAssessment(Macchinario macchinario) {
        // In pratica: qui succede il grosso (RAG + AI + parsing + salvataggio finale).
        log.info("Generating assessment for macchinario: {}", macchinario.getNome());
        validateAssessmentGenerationAllowed(macchinario);

        // 1) Prepariamo una query "furba" per trovare normativa pertinente.
        String ragQuery = buildRagQuery(macchinario);

        // 2) Recuperiamo i chunk normativi più vicini alla query.
        List<Document> relevantDocs = ragService.searchRelevantDocuments(ragQuery, EXPLAINABILITY_TOP_K);
        List<RetrievedChunk> retrievedChunks = toRetrievedChunks(relevantDocs);
        String context = buildExplainabilityContext(retrievedChunks);

        ChatClient chatClient = chatClientBuilder.build();

        // 3) Primo tentativo: output explainable con chunkId (via JSON).
        String userPrompt = buildExplainableAssessmentPrompt(macchinario, context);
        String response = chatClient.prompt()
                .system(ASSESSMENT_SYSTEM_PROMPT_EXPLAINABLE)
                .user(userPrompt)
                .call()
                .content();

        Optional<AssessmentResult> explainable = parseExplainableAssessmentResponse(response, macchinario, retrievedChunks);

        // 4) Piano B: se il JSON è malformato, usiamo il formato legacy testuale.
        AssessmentResult assessment = explainable.orElseGet(() -> {
            log.warn("Explainable JSON parsing failed, generating legacy assessment (no citations).");
            String legacyContext = ragService.buildContextFromDocuments(relevantDocs);
            List<String> documentSources = ragService.getDocumentSources(relevantDocs);
            String legacyPrompt = buildAssessmentPrompt(macchinario, legacyContext);

            String legacyResponse = chatClient.prompt()
                    .system(ASSESSMENT_SYSTEM_PROMPT)
                    .user(legacyPrompt)
                    .call()
                    .content();

            return parseAssessmentResponse(legacyResponse, macchinario, documentSources);
        });

        // 5) Persistiamo sempre un risultato finale (explainable o fallback).
        assessment = assessmentResultRepository.save(assessment);
        pruneAssessmentsForMachine(macchinario, assessment.getId());

        log.info("Assessment generated successfully for macchinario: {}", macchinario.getNome());
        return assessment;
    }

    private void validateAssessmentGenerationAllowed(Macchinario macchinario) {
        if (macchinario == null || macchinario.getId() == null) {
            return;
        }

        Optional<AssessmentResult> latestAssessment = assessmentResultRepository
                .findFirstByMacchinarioOrderByDataAssessmentDesc(macchinario);

        if (latestAssessment.isEmpty()) {
            return;
        }

        if (!hasMachineBeenModifiedSinceAssessment(macchinario, latestAssessment.get())) {
            throw new IllegalStateException(
                    "Esiste già un assessment aggiornato per questo macchinario. Modifica il macchinario prima di generarne un altro."
            );
        }
    }

    private boolean hasMachineBeenModifiedSinceAssessment(Macchinario macchinario, AssessmentResult latestAssessment) {
        if (macchinario == null || latestAssessment == null) {
            return true;
        }

        LocalDateTime latestAssessmentDate = latestAssessment.getDataAssessment();
        if (latestAssessmentDate == null) {
            return true;
        }

        LocalDateTime lastMachineChange = macchinario.getDataUltimaModifica() != null
                ? macchinario.getDataUltimaModifica()
                : macchinario.getDataCreazione();

        if (lastMachineChange == null) {
            return false;
        }

        return lastMachineChange.isAfter(latestAssessmentDate);
    }

    private void pruneAssessmentsForMachine(Macchinario macchinario, Long latestAssessmentId) {
        if (macchinario == null || macchinario.getId() == null || latestAssessmentId == null) {
            return;
        }

        List<AssessmentResult> assessments = assessmentResultRepository
                .findByMacchinarioIdOrderByDataAssessmentDesc(macchinario.getId());

        if (assessments.size() <= 1) {
            return;
        }

        List<AssessmentResult> staleAssessments = assessments.stream()
                .filter(existing -> existing != null && !Objects.equals(existing.getId(), latestAssessmentId))
                .toList();

        if (!staleAssessments.isEmpty()) {
            assessmentResultRepository.deleteAll(staleAssessments);
        }
    }

    /**
     * Cosa fa: costruisce query semantica RAG partendo dai dati macchina.
     * Perché esiste: migliora qualità retrieval includendo categoria, descrizione e parole chiave normative.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario) -> AssessmentService.buildRagQuery`.
     * Input/Output: input macchinario; output stringa query.
     * Eccezioni/edge case: descrizione nulla gestita con omissione.
     * Side effects: nessuno.
     *
     * @param macchinario macchina di riferimento.
     * @return query RAG.
     */
    private String buildRagQuery(Macchinario macchinario) {
        StringBuilder query = new StringBuilder();
        query.append("Requisiti di sicurezza e conformità per ");
        query.append(macchinario.getCategoria() != null ? macchinario.getCategoria() + " " : "");
        query.append(macchinario.getNome());

        if (macchinario.getDescrizione() != null) {
            query.append(". ").append(macchinario.getDescrizione());
        }

        query.append(" Regolamento Macchine sicurezza requisiti essenziali marcatura CE");

        return query.toString();
    }

    /**
     * Cosa fa: costruisce prompt legacy testuale per assessment non explainable.
     * Perché esiste: fallback compatibile quando il modello non restituisce JSON valido.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario)` nel ramo fallback.
     * Input/Output: input macchina + contesto; output prompt strutturato markdown.
     * Eccezioni/edge case: campi null sostituiti con placeholder "Non specificato/fornita".
     * Side effects: nessuno.
     *
     * @param macchinario dati macchina.
     * @param context contesto documentale RAG.
     * @return prompt legacy.
     */
    private String buildAssessmentPrompt(Macchinario macchinario, String context) {
        return String.format("""
                Analizza il seguente macchinario e fornisci un assessment di conformità basandoti sui documenti di riferimento.
                
                === INFORMAZIONI MACCHINARIO ===
                Nome: %s
                Produttore: %s
                Modello: %s
                Numero di Serie: %s
                Anno di Produzione: %s
                Categoria: %s
                Descrizione: %s
                Specifiche Tecniche: %s
                
                %s
                
                Fornisci l'assessment completo nel seguente formato:
                
                ## RIEPILOGO CONFORMITÀ
                [Inserisci qui il riepilogo]
                
                ## NON CONFORMITÀ RILEVATE
                [Elenca le non conformità identificate]
                
                ## RACCOMANDAZIONI
                [Elenca le raccomandazioni per ogni non conformità]
                
                ## LIVELLO DI RISCHIO
                [BASSO/MEDIO/ALTO/CRITICO]
                
                ## PUNTEGGIO DI CONFORMITÀ
                [Numero da 0 a 100]
                """,
                macchinario.getNome(),
                macchinario.getProduttore() != null ? macchinario.getProduttore() : "Non specificato",
                macchinario.getModello() != null ? macchinario.getModello() : "Non specificato",
                macchinario.getNumeroSerie() != null ? macchinario.getNumeroSerie() : "Non specificato",
                macchinario.getAnnoProduzione() != null ? macchinario.getAnnoProduzione().toString() : "Non specificato",
                macchinario.getCategoria() != null ? macchinario.getCategoria() : "Non specificata",
                macchinario.getDescrizione() != null ? macchinario.getDescrizione() : "Non fornita",
                macchinario.getSpecificheTecniche() != null ? macchinario.getSpecificheTecniche() : "Non fornite",
                context
        );
    }

    /**
     * Cosa fa: genera prompt JSON-first per assessment explainable con chunkId obbligatori.
     * Perché esiste: forza output verificabile e facilmente parsabile/persistibile.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario) -> AssessmentService.buildExplainableAssessmentPrompt`.
     * Input/Output: input macchina + contesto chunk; output prompt istruzionale per LLM.
     * Eccezioni/edge case: campi null sostituiti con placeholder.
     * Side effects: nessuno.
     *
     * @param macchinario dati macchina da valutare.
     * @param context contesto chunk normativi.
     * @return prompt explainable.
     */
    private String buildExplainableAssessmentPrompt(Macchinario macchinario, String context) {
        return String.format("""
                Analizza il seguente macchinario e fornisci un assessment di conformità basandoti esclusivamente sui chunk normativi forniti.

                === INFORMAZIONI MACCHINARIO ===
                Nome: %s
                Produttore: %s
                Modello: %s
                Numero di Serie: %s
                Anno di Produzione: %s
                Categoria: %s
                Descrizione: %s
                Specifiche Tecniche: %s

                %s

                Restituisci SOLO un JSON con questo schema:
                {
                  "riepilogoConformita": "string",
                  "nonConformitaRilevate": [{"text":"string","chunkIds":[1,2]}],
                  "raccomandazioni": [{"text":"string","chunkIds":[1,2]}],
                  "livelloRischio": "BASSO|MEDIO|ALTO|CRITICO",
                  "punteggioConformita": 0
                }

                Regole:
                - "chunkIds" deve contenere SOLO numeri presenti nell'elenco chunk (CHUNK 1..N).
                - Ogni punto dovrebbe avere almeno 1 chunkId se possibile.
                - Non includere il testo dei chunk nel JSON: solo gli ID.
                """,
                macchinario.getNome(),
                macchinario.getProduttore() != null ? macchinario.getProduttore() : "Non specificato",
                macchinario.getModello() != null ? macchinario.getModello() : "Non specificato",
                macchinario.getNumeroSerie() != null ? macchinario.getNumeroSerie() : "Non specificato",
                macchinario.getAnnoProduzione() != null ? macchinario.getAnnoProduzione().toString() : "Non specificato",
                macchinario.getCategoria() != null ? macchinario.getCategoria() : "Non specificata",
                macchinario.getDescrizione() != null ? macchinario.getDescrizione() : "Non fornita",
                macchinario.getSpecificheTecniche() != null ? macchinario.getSpecificheTecniche() : "Non fornite",
                context
        );
    }

    /**
     * Cosa fa: parse della risposta JSON explainable e mapping in `AssessmentResult` con fonti.
     * Perché esiste: trasforma output modello in formato persistibile e tracciabile.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario) -> AssessmentService.parseExplainableAssessmentResponse`.
     * Input/Output: input risposta raw + macchina + chunk recuperati; output `Optional<AssessmentResult>`.
     * Eccezioni/edge case: JSON non valido o serializzazione fallita => `Optional.empty()` (attiva fallback legacy).
     * Side effects: logging warning e creazione payload JSON explainable da salvare.
     *
     * @param response output raw del modello.
     * @param macchinario macchina analizzata.
     * @param retrievedChunks chunk disponibili per risoluzione fonti.
     * @return assessment explainable opzionale.
     */
    private Optional<AssessmentResult> parseExplainableAssessmentResponse(String response, Macchinario macchinario, List<RetrievedChunk> retrievedChunks) {
        JsonNode root;
        try {
            // Se il modello aggiunge testo extra, extractFirstJsonObject prova a salvare la parte utile.
            root = objectMapper.readTree(extractFirstJsonObject(response));
        } catch (Exception e) {
            log.warn("Unable to parse explainable assessment JSON: {}", e.getMessage());
            return Optional.empty();
        }

        String riepilogoConformita = safe(textValue(root, "riepilogoConformita"));
        String livelloRischio = textValue(root, "livelloRischio");
        Integer punteggio = intValue(root.get("punteggioConformita")).orElse(null);

        List<ExplainableFindingDraft> nonConformitaDrafts = parseFindingDrafts(root.get("nonConformitaRilevate"));
        List<ExplainableFindingDraft> raccomandazioniDrafts = parseFindingDrafts(root.get("raccomandazioni"));

        // Indice veloce chunkId -> chunk, come un dizionario.
        Map<Integer, RetrievedChunk> chunkById = retrievedChunks.stream()
                .collect(Collectors.toMap(RetrievedChunk::chunkId, c -> c));

        List<ExplainableFinding> nonConformita = toExplainableFindings(nonConformitaDrafts, chunkById);
        List<ExplainableFinding> raccomandazioni = toExplainableFindings(raccomandazioniDrafts, chunkById);

        String nonConformitaJson;
        String raccomandazioniJson;
        try {
            nonConformitaJson = objectMapper.writeValueAsString(nonConformita);
            raccomandazioniJson = objectMapper.writeValueAsString(raccomandazioni);
        } catch (JsonProcessingException e) {
            log.warn("Unable to serialize explainability payload: {}", e.getMessage());
            return Optional.empty();
        }

        AssessmentResult assessment = new AssessmentResult();
        assessment.setMacchinario(macchinario);
        assessment.setRisultato(response);
        assessment.setRiepilogoConformita(riepilogoConformita);
        assessment.setNonConformitaRilevate(nonConformitaJson);
        assessment.setRaccomandazioni(raccomandazioniJson);
        assessment.setLivelloRischio(normalizeRiskLevel(livelloRischio));
        assessment.setPunteggioConformita(normalizeScore(punteggio));

        // Raccogliamo i nomi documento usati davvero nelle fonti.
        Set<String> usedDocuments = new TreeSet<>();

        usedDocuments.addAll(nonConformita.stream()
                .flatMap(f -> f.sources().stream())
                .map(ExplainableSource::fileName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet()));

        usedDocuments.addAll(raccomandazioni.stream()
                .flatMap(f -> f.sources().stream())
                .map(ExplainableSource::fileName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toSet()));

        if (usedDocuments.isEmpty()) {
            // Edge case: se l'AI non ha citato fonti, usiamo almeno i documenti recuperati dal retriever.
            usedDocuments.addAll(retrievedChunks.stream()
                    .map(RetrievedChunk::fileName)
                    .filter(Objects::nonNull)
                    .filter(name -> !name.isBlank())
                    .collect(Collectors.toSet()));
        }

        assessment.setDocumentiUtilizzati(String.join(", ", usedDocuments));

        return Optional.of(assessment);
    }

    /**
     * Cosa fa: parse del vecchio formato testo markdown in campi strutturati assessment.
     * Perché esiste: mantiene retrocompatibilità quando explainable JSON non è disponibile.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario)` nel fallback legacy.
     * Input/Output: input risposta testo + macchina + fonti documento; output assessment non ancora salvato.
     * Eccezioni/edge case: punteggio non parsabile => default 50; livello rischio non riconosciuto => BASSO.
     * Side effects: nessuno oltre costruzione oggetto in memoria.
     *
     * @param response risposta legacy modello.
     * @param macchinario macchina analizzata.
     * @param documentSources documenti usati nel retrieval.
     * @return assessment popolato da output legacy.
     */
    private AssessmentResult parseAssessmentResponse(String response, Macchinario macchinario, List<String> documentSources) {
        AssessmentResult assessment = new AssessmentResult();
        assessment.setMacchinario(macchinario);
        assessment.setRisultato(response);

        // 1) Estraggo le macro-sezioni dal testo legacy.
        assessment.setRiepilogoConformita(extractSection(response, "RIEPILOGO CONFORMITÀ"));
        assessment.setNonConformitaRilevate(extractSection(response, "NON CONFORMITÀ RILEVATE"));
        assessment.setRaccomandazioni(extractSection(response, "RACCOMANDAZIONI"));

        // 2) Normalizzo il livello rischio cercando parole chiave.
        String livelloRischio = extractSection(response, "LIVELLO DI RISCHIO").trim().toUpperCase();
        if (livelloRischio.contains("CRITICO")) {
            assessment.setLivelloRischio("CRITICO");
        } else if (livelloRischio.contains("ALTO")) {
            assessment.setLivelloRischio("ALTO");
        } else if (livelloRischio.contains("MEDIO")) {
            assessment.setLivelloRischio("MEDIO");
        } else {
            assessment.setLivelloRischio("BASSO");
        }

        // 3) Ricavo un numero anche se il testo contiene frasi (es. "Punteggio: 74/100").
        String punteggioStr = extractSection(response, "PUNTEGGIO DI CONFORMITÀ");
        try {
            int punteggio = Integer.parseInt(punteggioStr.replaceAll("[^0-9]", ""));
            assessment.setPunteggioConformita(Math.min(100, Math.max(0, punteggio)));
        } catch (NumberFormatException e) {
            // Se non c'è un numero valido, usiamo un valore neutro.
            assessment.setPunteggioConformita(50); // Default
        }

        // 4) Salviamo le fonti testuali ricevute dal retrieval.
        assessment.setDocumentiUtilizzati(String.join(", ", documentSources));

        return assessment;
    }

    /**
     * Cosa fa: converte array JSON di finding grezzi in draft tipizzati.
     * Perché esiste: prepara dati intermedi per risoluzione chunkId -> fonti reali.
     * Chi lo chiama: `AssessmentService.parseExplainableAssessmentResponse -> AssessmentService.parseFindingDrafts`.
     * Input/Output: input nodo JSON; output lista `ExplainableFindingDraft`.
     * Eccezioni/edge case: nodo nullo/non array => lista vuota; item malformati saltati.
     * Side effects: nessuno.
     *
     * @param node nodo JSON da interpretare.
     * @return bozza finding.
     */
    private List<ExplainableFindingDraft> parseFindingDrafts(JsonNode node) {
        if (node == null || !node.isArray()) return List.of();

        List<ExplainableFindingDraft> drafts = new java.util.ArrayList<>();
        for (JsonNode item : node) {
            if (item == null || item.isNull()) continue;

            if (item.isTextual()) {
                // Compatibilità: alcuni modelli tornano direttamente una stringa invece di un oggetto.
                drafts.add(new ExplainableFindingDraft(item.asText(), List.of()));
                continue;
            }

            if (!item.isObject()) continue;

            String text = item.path("text").asText(null);
            if (text == null || text.isBlank()) {
                // Supporto schema alternativo in italiano.
                text = item.path("testo").asText("");
            }

            List<Integer> chunkIds = parseChunkIds(item.get("chunkIds"));
            drafts.add(new ExplainableFindingDraft(text, chunkIds));
        }

        return drafts;
    }

    /**
     * Cosa fa: legge un array JSON di chunkIds e restituisce interi univoci ordinati d'inserimento.
     * Perché esiste: normalizza input modello prima della risoluzione fonti.
     * Chi lo chiama: `parseFindingDrafts` e `parseExplainableAssessmentResponse`.
     * Input/Output: input nodo JSON; output lista di ID.
     * Eccezioni/edge case: nodo nullo/non array => lista vuota.
     * Side effects: nessuno.
     *
     * @param node nodo chunkIds.
     * @return lista id validi.
     */
    private List<Integer> parseChunkIds(JsonNode node) {
        if (node == null || !node.isArray()) return List.of();

        Set<Integer> ids = new java.util.LinkedHashSet<>();
        for (JsonNode item : node) {
            intValue(item).ifPresent(ids::add);
        }

        return List.copyOf(ids);
    }

    /**
     * Cosa fa: converte un `JsonNode` eterogeneo in intero opzionale.
     * Perché esiste: supporta numeri e stringhe numeriche nei payload AI.
     * Chi lo chiama: `parseChunkIds` e `parseExplainableAssessmentResponse`.
     * Input/Output: input nodo; output `Optional<Integer>`.
     * Eccezioni/edge case: valori non interpretabili => empty.
     * Side effects: nessuno.
     *
     * @param node nodo da convertire.
     * @return intero opzionale.
     */
    private Optional<Integer> intValue(JsonNode node) {
        if (node == null || node.isNull()) return Optional.empty();
        if (node.isInt() || node.isLong()) return Optional.of(node.asInt());
        if (node.isNumber()) return Optional.of((int) Math.round(node.asDouble()));

        if (node.isTextual()) {
            String digits = node.asText().replaceAll("[^0-9-]", "");
            if (digits.isBlank()) return Optional.empty();
            try {
                return Optional.of(Integer.parseInt(digits));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Cosa fa: legge in sicurezza un campo testuale da nodo JSON.
     * Perché esiste: evita NPE e centralizza la logica di accesso campi opzionali.
     * Chi lo chiama: `parseExplainableAssessmentResponse`.
     * Input/Output: input root + nome campo; output stringa o `null`.
     * Eccezioni/edge case: root/campo null => `null`.
     * Side effects: nessuno.
     *
     * @param root nodo JSON root.
     * @param fieldName campo da leggere.
     * @return valore testuale o `null`.
     */
    private String textValue(JsonNode root, String fieldName) {
        if (root == null || fieldName == null) return null;
        JsonNode node = root.get(fieldName);
        if (node == null || node.isNull()) return null;
        return node.asText(null);
    }

    /**
     * Cosa fa: trasforma documenti RAG in chunk interni con metadati uniformi.
     * Perché esiste: prepara dati explainable per prompt e mapping fonti.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario) -> AssessmentService.toRetrievedChunks`.
     * Input/Output: input lista documenti; output lista chunk arricchiti.
     * Eccezioni/edge case: lista vuota/null => lista vuota.
     * Side effects: nessuno.
     *
     * @param documents chunk recuperati.
     * @return chunk normalizzati.
     */
    private List<RetrievedChunk> toRetrievedChunks(List<Document> documents) {
        if (documents == null || documents.isEmpty()) return List.of();

        return java.util.stream.IntStream.range(0, documents.size())
                .mapToObj(i -> {
                    Document doc = documents.get(i);
                    Map<String, Object> metadata = doc.getMetadata();
                    // Metadati non uniformi: prendiamo la prima chiave disponibile.
                    String fileName = Optional.ofNullable(metadata.get("fileName"))
                            .or(() -> Optional.ofNullable(metadata.get("file_name")))
                            .or(() -> Optional.ofNullable(metadata.get("filename")))
                            .map(Object::toString)
                            .orElse("Documento");

                    Integer page = parseInteger(metadata.get("page"))
                            .or(() -> parseInteger(metadata.get("page_number")))
                            .or(() -> parseInteger(metadata.get("pageNumber")))
                            .orElse(null);

                    Double confidence = doc.getScore();
                    if (confidence == null) {
                        // Fallback semplice: più il chunk è in alto nel ranking, più confidence è alta.
                        confidence = rankBasedConfidence(i);
                    }

                    return new RetrievedChunk(i + 1, fileName, page, doc.getText(), confidence);
                })
                .toList();
    }

    /**
     * Cosa fa: serializza chunk normativi in contesto testuale da passare all'LLM.
     * Perché esiste: vincola il modello a fonti esplicite con id numerico tracciabile.
     * Chi lo chiama: `AssessmentService.generateAssessment(Macchinario) -> AssessmentService.buildExplainabilityContext`.
     * Input/Output: input lista chunk; output stringa contesto.
     * Eccezioni/edge case: lista vuota => messaggio esplicito "Nessun documento".
     * Side effects: nessuno.
     *
     * @param chunks chunk da serializzare nel prompt.
     * @return contesto explainability.
     */
    private String buildExplainabilityContext(List<RetrievedChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return "=== CHUNKS NORMATIVI RECUPERATI (RAG) ===\n\nNessun documento di riferimento trovato nel database.\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== CHUNKS NORMATIVI RECUPERATI (RAG) ===\n");
        sb.append("Usa SOLO questi chunk come fonti. Per citare un chunk usa il suo chunkId (numero).\n\n");

        for (RetrievedChunk chunk : chunks) {
            sb.append("[CHUNK ").append(chunk.chunkId()).append("] ");
            sb.append("Fonte: ").append(chunk.fileName());
            if (chunk.page() != null) {
                sb.append(" | Pagina: ").append(chunk.page());
            }
            if (chunk.confidence() != null) {
                sb.append(" | Pertinenza: ").append(String.format(Locale.ROOT, "%.2f", chunk.confidence()));
            }
            sb.append("\n");
            sb.append(chunk.text()).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Cosa fa: converte finding draft in finding explainable risolvendo i chunkId in fonti reali.
     * Perché esiste: arricchisce output AI con riferimenti documentali concreti per auditabilità.
     * Chi lo chiama: `AssessmentService.parseExplainableAssessmentResponse -> AssessmentService.toExplainableFindings`.
     * Input/Output: input drafts + mappa chunk; output lista `ExplainableFinding`.
     * Eccezioni/edge case: draft vuoti/null => lista vuota; chunk id mancanti ignorati.
     * Side effects: nessuno.
     *
     * @param drafts finding grezzi dal modello.
     * @param chunkById mappa id->chunk disponibile.
     * @return finding explainable pronti.
     */
    private List<ExplainableFinding> toExplainableFindings(List<ExplainableFindingDraft> drafts, Map<Integer, RetrievedChunk> chunkById) {
        if (drafts == null || drafts.isEmpty()) return List.of();

        return drafts.stream()
                .filter(Objects::nonNull)
                .map(draft -> {
                    List<Integer> chunkIds = draft.chunkIds() != null ? draft.chunkIds() : List.of();
                    List<ExplainableSource> sources = chunkIds.stream()
                            .filter(Objects::nonNull)
                            .distinct()
                            .map(chunkById::get)
                            .filter(Objects::nonNull)
                            .map(this::toExplainableSource)
                            .toList();

                    return new ExplainableFinding(safe(draft.text()), sources);
                })
                .toList();
    }

    /**
     * Cosa fa: crea un `ExplainableSource` da un chunk interno.
     * Perché esiste: centralizza formato fonte usato in non conformità/raccomandazioni.
     * Chi lo chiama: `AssessmentService.toExplainableFindings -> AssessmentService.toExplainableSource`.
     * Input/Output: input chunk; output fonte explainable.
     * Eccezioni/edge case: nessuna gestione dedicata (presuppone chunk non nullo).
     * Side effects: nessuno.
     *
     * @param chunk chunk da convertire.
     * @return fonte explainable.
     */
    private ExplainableSource toExplainableSource(RetrievedChunk chunk) {
        String reference = buildReference(chunk.fileName(), chunk.page(), chunk.text());
        return new ExplainableSource(reference, chunk.fileName(), chunk.page(), chunk.text(), chunk.confidence());
    }

    /**
     * Cosa fa: costruisce riferimento leggibile includendo articolo/paragrafo/comma/pagina se trovati.
     * Perché esiste: migliora comprensibilità normativa delle fonti mostrate all'utente.
     * Chi lo chiama: `AssessmentService.toExplainableSource -> AssessmentService.buildReference`.
     * Input/Output: input nome file, pagina e testo chunk; output riferimento descrittivo.
     * Eccezioni/edge case: assenza pattern => riferimento ridotto a file (+ eventuale pagina).
     * Side effects: nessuno.
     *
     * @param fileName nome documento.
     * @param page pagina chunk.
     * @param chunkText testo chunk da cui estrarre pattern normativi.
     * @return riferimento formattato.
     */
    private String buildReference(String fileName, Integer page, String chunkText) {
        String base = (fileName == null || fileName.isBlank()) ? "Documento" : fileName.trim();

        String articolo = firstMatch(ARTICLE_PATTERN, chunkText);
        String paragrafo = firstMatch(PARAGRAPH_PATTERN, chunkText);
        String comma = firstMatch(COMMA_PATTERN, chunkText);

        StringBuilder sb = new StringBuilder(base);
        if (articolo != null) {
            sb.append(", Articolo ").append(articolo);
        }
        if (paragrafo != null) {
            sb.append(", Paragrafo ").append(paragrafo);
        }
        if (comma != null) {
            sb.append(", Comma ").append(comma);
        }
        if (page != null) {
            sb.append(", Pag. ").append(page);
        }

        return sb.toString();
    }

    /**
     * Cosa fa: restituisce il primo gruppo regex trovato nel testo.
     * Perché esiste: riusa logica estrazione pattern normativi senza duplicazione.
     * Chi lo chiama: `AssessmentService.buildReference -> AssessmentService.firstMatch`.
     * Input/Output: input pattern + testo; output gruppo trovato o `null`.
     * Eccezioni/edge case: input nullo/blank o nessun match => `null`.
     * Side effects: nessuno.
     *
     * @param pattern regex da applicare.
     * @param input testo sorgente.
     * @return valore del primo match o `null`.
     */
    private String firstMatch(Pattern pattern, String input) {
        if (input == null || input.isBlank()) return null;
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) return null;
        String value = matcher.group(1);
        return value != null && !value.isBlank() ? value.trim() : null;
    }

    /**
     * Cosa fa: converte valore generico metadati in intero opzionale.
     * Perché esiste: gestisce vari formati pagina nei metadata documentali.
     * Chi lo chiama: `AssessmentService.toRetrievedChunks -> AssessmentService.parseInteger`.
     * Input/Output: input oggetto; output integer opzionale.
     * Eccezioni/edge case: valori non numerici => empty.
     * Side effects: nessuno.
     *
     * @param value valore da convertire.
     * @return intero opzionale.
     */
    private Optional<Integer> parseInteger(Object value) {
        if (value == null) return Optional.empty();
        if (value instanceof Number number) return Optional.of(number.intValue());

        String digits = value.toString().replaceAll("[^0-9-]", "");
        if (digits.isBlank()) return Optional.empty();
        try {
            return Optional.of(Integer.parseInt(digits));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Cosa fa: produce confidence fallback in base alla posizione del risultato.
     * Perché esiste: mantiene un valore di pertinenza anche quando il retriever non fornisce score.
     * Chi lo chiama: `AssessmentService.toRetrievedChunks -> AssessmentService.rankBasedConfidence`.
     * Input/Output: input indice rank; output double clampato.
     * Eccezioni/edge case: nessuna.
     * Side effects: nessuno.
     *
     * @param rankIndex posizione del chunk.
     * @return confidence stimata.
     */
    private Double rankBasedConfidence(int rankIndex) {
        double value = 0.9 - (rankIndex * 0.05);
        return Math.max(0.5, Math.min(0.95, value));
    }

    /**
     * Cosa fa: estrae il primo blocco JSON delimitato da parentesi graffe.
     * Perché esiste: rende più robusto il parsing quando il modello include testo extra.
     * Chi lo chiama: `AssessmentService.parseExplainableAssessmentResponse -> AssessmentService.extractFirstJsonObject`.
     * Input/Output: input testo raw; output stringa JSON o `{}` fallback.
     * Eccezioni/edge case: delimitatori assenti => `{}`.
     * Side effects: nessuno.
     *
     * @param raw output modello non normalizzato.
     * @return oggetto JSON estratto.
     */
    private String extractFirstJsonObject(String raw) {
        if (raw == null) return "{}";
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) return "{}";
        return raw.substring(start, end + 1).trim();
    }

    /**
     * Cosa fa: normalizza stringa null/blank in stringa vuota trimmed.
     * Perché esiste: evita campi null in payload testuali dell'assessment.
     * Chi lo chiama: `parseExplainableAssessmentResponse` e `toExplainableFindings`.
     * Input/Output: input valore; output valore safe.
     * Eccezioni/edge case: nessuna.
     * Side effects: nessuno.
     *
     * @param value valore da normalizzare.
     * @return valore safe.
     */
    private String safe(String value) {
        if (value == null) return "";
        String trimmed = value.trim();
        return trimmed.isEmpty() ? "" : trimmed;
    }

    /**
     * Cosa fa: normalizza il livello rischio ai quattro valori supportati.
     * Perché esiste: garantisce coerenza dominio anche con output AI variabile.
     * Chi lo chiama: `parseExplainableAssessmentResponse`.
     * Input/Output: input livello libero; output `BASSO|MEDIO|ALTO|CRITICO`.
     * Eccezioni/edge case: valore non riconosciuto => `MEDIO`.
     * Side effects: nessuno.
     *
     * @param value livello rischio da normalizzare.
     * @return livello rischio normalizzato.
     */
    private String normalizeRiskLevel(String value) {
        if (value == null) return "MEDIO";
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("CRITICO")) return "CRITICO";
        if (normalized.contains("ALTO")) return "ALTO";
        if (normalized.contains("MEDIO")) return "MEDIO";
        if (normalized.contains("BASSO")) return "BASSO";
        return "MEDIO";
    }

    /**
     * Cosa fa: limita il punteggio conformità nell'intervallo 0..100.
     * Perché esiste: protegge persistenza e UI da valori fuori range.
     * Chi lo chiama: `parseExplainableAssessmentResponse`.
     * Input/Output: input punteggio opzionale; output punteggio valido.
     * Eccezioni/edge case: input nullo => default 50.
     * Side effects: nessuno.
     *
     * @param value punteggio da normalizzare.
     * @return punteggio clampato.
     */
    private Integer normalizeScore(Integer value) {
        if (value == null) return 50;
        return Math.min(100, Math.max(0, value));
    }

    /**
     * Cosa fa: estrae il contenuto di una sezione marcata (`##` o `**`) da testo assessment legacy.
     * Perché esiste: consente parsing strutturato di risposte markdown non JSON.
     * Chi lo chiama: `AssessmentService.parseAssessmentResponse -> AssessmentService.extractSection`.
     * Input/Output: input testo completo + nome sezione; output contenuto sezione o stringa vuota.
     * Eccezioni/edge case: sezione non trovata => stringa vuota.
     * Side effects: nessuno.
     *
     * @param text risposta legacy completa.
     * @param sectionName nome sezione da individuare.
     * @return contenuto estratto.
     */
    private String extractSection(String text, String sectionName) {
        // Cerchiamo prima un titolo markdown classico.
        String pattern = "## " + sectionName;
        int startIndex = text.indexOf(pattern);
        if (startIndex == -1) {
            // Fallback: alcune risposte usano titoli in grassetto.
            pattern = "**" + sectionName + "**";
            startIndex = text.indexOf(pattern);
        }
        if (startIndex == -1) {
            return "";
        }

        startIndex = text.indexOf("\n", startIndex);
        if (startIndex == -1) return "";

        int endIndex = text.indexOf("\n## ", startIndex + 1);
        if (endIndex == -1) {
            endIndex = text.indexOf("\n**", startIndex + 1);
        }
        if (endIndex == -1) {
            endIndex = text.length();
        }

        return text.substring(startIndex, endIndex).trim();
    }

    /**
     * Cosa fa: recupera tutti gli assessment di un macchinario ordinati per data decrescente.
     * Perché esiste: supporta storico valutazioni nel controller macchinari.
     * Chi lo chiama: `MacchinarioController.getAssessments` e `MacchinarioController.downloadLatestAssessmentPdf`.
     * Input/Output: input ID macchinario; output lista assessment.
     * Eccezioni/edge case: nessun risultato => lista vuota.
     * Side effects: nessuno, sola lettura repository.
     *
     * @param macchinarioId ID macchinario.
     * @return storico assessment.
     */
    public List<AssessmentResult> getAssessmentsByMacchinario(Long macchinarioId) {
        // Restituisce solo l'ultimo assessment disponibile per la macchina.
        return assessmentResultRepository.findFirstByMacchinarioIdOrderByDataAssessmentDesc(macchinarioId)
                .map(List::of)
                .orElseGet(List::of);
    }

    /**
     * Cosa fa: recupera un assessment per ID o lancia errore se assente.
     * Perché esiste: lookup centralizzato usato da controller e chat contestuale.
     * Chi lo chiama: `AssessmentController.getAssessment -> AssessmentService.getAssessment`.
     * Input/Output: input ID assessment; output entità `AssessmentResult`.
     * Eccezioni/edge case: ID non presente => `RuntimeException`.
     * Side effects: nessuno.
     *
     * @param id ID assessment.
     * @return assessment trovato.
     */
    public AssessmentResult getAssessment(Long id) {
        // Cerca 1 assessment per ID, altrimenti errore.
        return assessmentResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment non trovato con ID: " + id));
    }

    /**
     * Cosa fa: restituisce tutti gli assessment dell'app ordinati per data.
     * Perché esiste: alimenta endpoint globale di consultazione storico.
     * Chi lo chiama: `AssessmentController.getAllAssessments -> AssessmentService.getAllAssessments`.
     * Input/Output: nessun input; output lista assessment.
     * Eccezioni/edge case: lista vuota se nessun dato.
     * Side effects: nessuno.
     *
     * @return lista assessment ordinata.
     */
    public List<AssessmentResult> getAllAssessments() {
        // Elenco assessment globale con al massimo un record (il piu' recente) per macchinario.
        List<AssessmentResult> orderedAssessments = assessmentResultRepository.findAllByOrderByDataAssessmentDesc();
        List<AssessmentResult> latestPerMachine = new ArrayList<>();
        Set<Long> seenMachineIds = new HashSet<>();

        for (AssessmentResult assessment : orderedAssessments) {
            if (assessment == null || assessment.getMacchinario() == null || assessment.getMacchinario().getId() == null) {
                continue;
            }

            Long machineId = assessment.getMacchinario().getId();
            if (seenMachineIds.add(machineId)) {
                latestPerMachine.add(assessment);
            }
        }

        return latestPerMachine;
    }

    @Transactional
    public void deleteAssessment(Long assessmentId) {
        AssessmentResult assessment = getAssessment(assessmentId);
        Long machineId = assessment.getMacchinario() != null ? assessment.getMacchinario().getId() : null;

        if (machineId == null) {
            assessmentResultRepository.delete(assessment);
            return;
        }

        assessmentResultRepository.deleteAllByMacchinarioId(machineId);
    }
}
