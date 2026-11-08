package it.unicas.spring.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Service infrastrutturale per Retrieval-Augmented Generation (RAG).
 * Ruolo architetturale: incapsula accesso al `VectorStore`, ricerca semantica
 * e costruzione del contesto testuale da passare ai modelli LLM.
 * Interagisce con `ChatbotService` e `AssessmentService`.
 */
public class RagService {

    /*
     * - Questo e' il "motore di ricerca" semantico dei documenti caricati.
     * - Tu gli dai una domanda, lui prova a trovare i pezzi di testo piu' vicini come significato.
     * - Se serve, questi pezzi vengono uniti in un unico contesto da dare all'AI.
     * - In pratica: niente magie, e' una ricerca per somiglianza sul vector store.
     */
    /*
     * Cheat sheet super rapido (cosa dire a voce):
     * - `vectorStore`: e' il database vettoriale dove stanno i chunk gia' embeddati.
     * - `DEFAULT_TOP_K`: quanti risultati prendo di default.
     * - `DEFAULT_SIMILARITY_THRESHOLD`: filtro minimo di somiglianza (sotto, scarto).
     *
     * Metodi:
     * - `searchRelevantDocuments(query)`: scorciatoia con topK standard.
     * - `searchRelevantDocuments(query, topK)`: vera ricerca semantica nel vector store.
     * - `buildContext(query)`: cerca e poi aggrega in testo unico.
     * - `buildContextFromDocuments(documents)`: formatta i chunk in blocchi leggibili.
     * - `getDocumentSources(documents)`: estrae i nomi documento senza doppioni.
     *
     * Frase da interrogazione:
     * "Questo service non risponde all'utente: prepara il materiale giusto da dare ai modelli AI."
     */
    /*
     * Mappa operativa ultra pratica (dal punto di vista runtime):
     *
     * Scenario A - chat o assessment chiedono retrieval:
     * - il chiamante passa una query testuale (domanda o richiesta normativa).
     * - RagService costruisce `SearchRequest` con topK + soglia similarita'.
     * - VectorStore calcola vicinanza semantica tra query e chunk indicizzati.
     * - ritorna una lista di `Document` ordinata per pertinenza.
     *
     * Scenario B - serve contesto testuale pronto:
     * - RagService prende la lista `Document`.
     * - per ogni documento scrive metadati utili (fonte/pagina, se presenti).
     * - concatena il testo chunk in una stringa unica da passare al prompt.
     *
     * Scenario C - servono solo fonti:
     * - RagService estrae i nomi file dai metadati con fallback robusti.
     * - rimuove duplicati mantenendo solo fonti distinte.
     *
     * Decisioni importanti che vedi nel codice:
     * - soglia `DEFAULT_SIMILARITY_THRESHOLD`: evita chunk troppo rumorosi.
     * - fallback metadati (`fileName`, `file_name`, `filename`, `source`).
     * - fallback testo quando non ci sono documenti trovati.
     *
     * Risultato finale:
     * - i service AI ricevono un contesto piu' pulito e piu' verificabile.
     * - se il retrieval non trova nulla, il sistema lo esplicita senza inventare.
     */

    private final VectorStore vectorStore;

    // Numero risultati default e soglia minima di similarita'.
    private static final int DEFAULT_TOP_K = 5;
    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.7;

    /**
     * Cosa fa: esegue ricerca semantica con parametri default (`topK` predefinito).
     * Perché esiste: fornisce scorciatoia semplice per i chiamanti che non vogliono decidere `topK`.
     * Chi lo chiama: `RagService.buildContext -> RagService.searchRelevantDocuments(String)`.
     * Input/Output: input query testuale; output lista chunk `Document` ordinati per pertinenza.
     * Eccezioni/edge case: query vuota/non pertinente può restituire lista vuota.
     * Side effects: I/O su vector store (lettura) e log applicativo.
     *
     * @param query testo usato per il retrieval semantico.
     * @return documenti più pertinenti trovati nel vector store.
     */
    public List<Document> searchRelevantDocuments(String query) {
        // Versione semplice: usa la ricerca standard e prende i primi risultati utili.
        return searchRelevantDocuments(query, DEFAULT_TOP_K);
    }

    /**
     * Cosa fa: esegue ricerca semantica personalizzando il numero massimo di risultati.
     * Perché esiste: consente ai service chiamanti di modulare profondità del contesto.
     * Chi lo chiama: `ChatbotService.chat -> RagService.searchRelevantDocuments(String,int)`,
     * `AssessmentService.generateAssessment -> RagService.searchRelevantDocuments(String,int)`,
     * `RagService.searchRelevantDocuments(String) -> RagService.searchRelevantDocuments(String,int)`.
     * Input/Output: query + `topK`; output lista `Document` recuperati.
     * Eccezioni/edge case: con similarità sotto soglia può restituire lista vuota.
     * Side effects: interrogazione vector store e logging.
     *
     * @param query richiesta testuale da cercare.
     * @param topK numero massimo di chunk da recuperare.
     * @return lista documenti rilevanti.
     */
    public List<Document> searchRelevantDocuments(String query, int topK) {
        // Versione semplice: facciamo una ricerca per significato, non per parola esatta.
        log.info("Searching for relevant documents with query: {}", query);

        // Il retriever funziona come una ricerca "per somiglianza":
        // non cerca parole identiche, ma i pezzi di testo più vicini al significato.
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(DEFAULT_SIMILARITY_THRESHOLD)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        log.info("Found {} relevant documents", results.size());
        return results;
    }

    /**
     * Cosa fa: recupera documenti rilevanti e li concatena in un contesto unico.
     * Perché esiste: semplifica i chiamanti che vogliono direttamente il testo contestuale pronto.
     * Chi lo chiama: Non è stato trovato un chiamante diretto nel codice corrente.
     * Ipotesi: possibile utilizzo futuro/rifattorizzazione per prompt building centralizzato.
     * Input/Output: input query; output stringa contestuale multi-documento.
     * Eccezioni/edge case: nessun risultato => messaggio fallback leggibile.
     * Side effects: lettura vector store indiretta.
     *
     * @param query query semantica di partenza.
     * @return contesto testuale aggregato.
     */
    public String buildContext(String query) {
        // Versione semplice: cerca i documenti e li impacchetta in un testo unico.
        List<Document> relevantDocs = searchRelevantDocuments(query);
        return buildContextFromDocuments(relevantDocs);
    }

    /**
     * Cosa fa: converte una lista di chunk in testo contestuale strutturato con metadati fonte.
     * Perché esiste: uniforma il formato del contesto inviato ai modelli AI.
     * Chi lo chiama: `RagService.buildContext -> RagService.buildContextFromDocuments` e
     * `AssessmentService.generateAssessment (fallback legacy) -> RagService.buildContextFromDocuments`.
     * Input/Output: input lista documenti; output stringa con sezioni documento/pagina/contenuto.
     * Eccezioni/edge case: lista vuota => messaggio esplicito di assenza documenti.
     * Side effects: nessuno, elaborazione in memoria.
     *
     * @param documents chunk recuperati da vector store.
     * @return contesto formattato pronto per prompt.
     */
    public String buildContextFromDocuments(List<Document> documents) {
        // Versione semplice: da lista documenti -> stringa lunga pronta da dare all'AI.
        // Se non arriva nulla (lista vuota), l'AI riceve un messaggio esplicito
        // invece di un contesto ambiguo.
        if (documents == null || documents.isEmpty()) {
            return "Nessun documento di riferimento trovato nel database.";
        }

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("=== DOCUMENTI DI RIFERIMENTO ===\n\n");

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            contextBuilder.append(String.format("--- Documento %d ---\n", i + 1));

            // I metadati non sono sempre uguali tra reader diversi: controlliamo
            // prima la chiave e scriviamo solo quello che esiste davvero.
            if (doc.getMetadata().containsKey("fileName")) {
                contextBuilder.append("Fonte: ").append(doc.getMetadata().get("fileName")).append("\n");
            }
            if (doc.getMetadata().containsKey("page")) {
                contextBuilder.append("Pagina: ").append(doc.getMetadata().get("page")).append("\n");
            }

            contextBuilder.append("Contenuto:\n").append(doc.getText()).append("\n\n");
        }

        return contextBuilder.toString();
    }

    /**
     * Cosa fa: estrae i nomi sorgente unici dai metadati dei documenti recuperati.
     * Perché esiste: consente di tracciare quali documenti hanno contribuito all'output finale.
     * Chi lo chiama: `AssessmentService.generateAssessment (fallback legacy) -> RagService.getDocumentSources`.
     * Input/Output: input lista documenti; output lista nomi file distinti.
     * Eccezioni/edge case: metadati incompleti => fallback a "Documento sconosciuto".
     * Side effects: logging metadati (utile debug, aumenta rumore log in DEBUG/INFO).
     *
     * @param documents chunk da cui leggere i metadati sorgente.
     * @return elenco nomi documenti senza duplicati.
     */
    public List<String> getDocumentSources(List<Document> documents) {
        // Versione semplice: tira fuori solo i nomi file, senza doppioni.
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }

        return documents.stream()
                .map(doc -> {
                    log.info("Document metadata: {}", doc.getMetadata());  // Debug
                    Map<String, Object> metadata = doc.getMetadata();
                    // E' come cercare lo stesso dato in etichette diverse
                    // (fileName, file_name, filename...): prendiamo la prima utile.
                    if (metadata.containsKey("fileName")) {
                        return metadata.get("fileName").toString();
                    } else if (metadata.containsKey("file_name")) {
                        return metadata.get("file_name").toString();
                    } else if (metadata.containsKey("filename")) {
                        return metadata.get("filename").toString();
                    } else if (metadata.containsKey("source")) {
                        return metadata.get("source").toString();
                    }
                    return "Documento sconosciuto";
                })
                .distinct()
                .toList();
    }
}
