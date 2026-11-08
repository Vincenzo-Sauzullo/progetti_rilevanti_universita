package it.unicas.spring.springai.controller;

import it.unicas.spring.springai.model.AssessmentResult;
import it.unicas.spring.springai.model.Macchinario;
import it.unicas.spring.springai.service.AssessmentPdfService;
import it.unicas.spring.springai.service.AssessmentService;
import it.unicas.spring.springai.service.MachineManualExtractionService;
import it.unicas.spring.springai.service.MacchinarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per il dominio "Macchinario" e per i flussi di assessment collegati.
 * Ruolo architetturale: espone endpoint CRUD, estrazione dati da manuale PDF,
 * generazione assessment AI e download report PDF.
 * Interagisce con i service di dominio ({@link MacchinarioService}), AI ({@link AssessmentService},
 * {@link MachineManualExtractionService}) e document generation ({@link AssessmentPdfService}).
 */
@RestController
@RequestMapping("/api/macchinari")
@RequiredArgsConstructor
@Slf4j
public class MacchinarioController {

    private final MacchinarioService macchinarioService;
    private final AssessmentService assessmentService;
    private final MachineManualExtractionService machineManualExtractionService;
    private final AssessmentPdfService assessmentPdfService;

    /**
     * Cosa fa: valida e crea un nuovo macchinario persistendolo a DB.
     * Perché esiste: avvia il ciclo applicativo (solo macchine registrate possono avere assessment).
     * Chi lo chiama: endpoint `POST /api/macchinari`; test principali:
     * `MacchinarioRequiredFieldsIntegrationTest.* -> MacchinarioController.createMacchinario` e
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> MacchinarioController.createMacchinario`.
     * Input/Output: riceve JSON `Macchinario`; restituisce entità creata o errore validazione.
     * Eccezioni/edge case: nome nullo/vuoto => 400; trimming automatico prima del salvataggio.
     * Side effects: scrittura DB tramite `MacchinarioService.create`.
     *
     * @param macchinario payload del macchinario da registrare.
     * @return macchinario creato o risposta errore.
     */
    @PostMapping
    public ResponseEntity<?> createMacchinario(@RequestBody Macchinario macchinario) {
        if (macchinario == null || macchinario.getNome() == null || macchinario.getNome().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il nome è obbligatorio"));
        }

        macchinario.setNome(macchinario.getNome().trim()); //trim taglia gli spazi bianchi
        Macchinario created = macchinarioService.create(macchinario);
        return ResponseEntity.ok(created);
    }

    /**
     * Cosa fa: restituisce l'elenco completo dei macchinari disponibili.
     * Perché esiste: fornisce la base dati per dashboard/lista selezione macchine.
     * Chi lo chiama: endpoint `GET /api/macchinari`; non è stato trovato un chiamante Java diretto.
     * Input/Output: nessun input; output lista `Macchinario` ordinata.
     * Eccezioni/edge case: lista vuota restituita senza errore.
     * Side effects: sola lettura DB (`MacchinarioService.getAll`).
     *
     * @return lista macchinari.
     */
    @GetMapping
    public ResponseEntity<List<Macchinario>> getAllMacchinari() {
        return ResponseEntity.ok(macchinarioService.getAll());
    }

    /**
     * Cosa fa: recupera i dettagli di un singolo macchinario per ID.
     * Perché esiste: permette al client di aprire/modificare un record specifico.
     * Chi lo chiama: endpoint `GET /api/macchinari/{id}`; chiamante Java diretto non trovato.
     * Input/Output: input ID; output `Macchinario` o 404.
     * Eccezioni/edge case: ID inesistente => `RuntimeException` gestita come Not Found.
     * Side effects: nessuno, sola lettura repository.
     *
     * @param id identificativo macchinario.
     * @return dettaglio macchinario o 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Macchinario> getMacchinario(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(macchinarioService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: aggiorna i campi di un macchinario già esistente.
     * Perché esiste: permette correzione/anagrafica continua dei dati macchina.
     * Chi lo chiama: endpoint `PUT /api/macchinari/{id}`; non è stato trovato un chiamante Java diretto.
     * Input/Output: input ID + payload aggiornato; output record aggiornato, 400 o 404.
     * Eccezioni/edge case: nome nullo/vuoto => 400; ID inesistente => 404.
     * Side effects: scrittura DB (`MacchinarioService.update`).
     *
     * @param id ID del macchinario da aggiornare.
     * @param macchinario nuovi valori richiesti.
     * @return record aggiornato o errore.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMacchinario(@PathVariable Long id, @RequestBody Macchinario macchinario) {
        if (macchinario == null || macchinario.getNome() == null || macchinario.getNome().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il nome è obbligatorio"));
        }

        macchinario.setNome(macchinario.getNome().trim());
        try {
            return ResponseEntity.ok(macchinarioService.update(id, macchinario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: elimina un macchinario dal database.
     * Perché esiste: consente rimozione di record obsoleti o inseriti per errore.
     * Chi lo chiama: endpoint `DELETE /api/macchinari/{id}`; chiamante Java diretto non trovato.
     * Input/Output: input ID; output messaggio di conferma o 404.
     * Eccezioni/edge case: ID non presente => 404.
     * Side effects: cancellazione record via `MacchinarioService.delete`.
     *
     * @param id ID del macchinario da eliminare.
     * @return esito cancellazione.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMacchinario(@PathVariable Long id) {
        try {
            macchinarioService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Macchinario eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: ricerca macchinari per porzione di nome (case-insensitive).
     * Perché esiste: velocizza la selezione in archivi numerosi.
     * Chi lo chiama: endpoint `GET /api/macchinari/search`; chiamante Java diretto non trovato.
     * Input/Output: input query `nome`; output lista risultati filtrati.
     * Eccezioni/edge case: query vuota produce comportamento repository (possibile lista ampia).
     * Side effects: nessuno, sola lettura DB.
     *
     * @param nome stringa di filtro sul nome.
     * @return macchinari che soddisfano il filtro.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Macchinario>> searchMacchinari(@RequestParam String nome) {
        return ResponseEntity.ok(macchinarioService.searchByName(nome));
    }

    /**
     * Cosa fa: restituisce i macchinari appartenenti alla categoria indicata.
     * Perché esiste: supporta navigazione per tipologia macchina.
     * Chi lo chiama: endpoint `GET /api/macchinari/categoria/{categoria}`; chiamante Java diretto non trovato.
     * Input/Output: input categoria; output lista macchine filtrate.
     * Eccezioni/edge case: categoria non presente restituisce lista vuota.
     * Side effects: nessuno, interrogazione repository in sola lettura.
     *
     * @param categoria categoria richiesta.
     * @return lista macchinari di quella categoria.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Macchinario>> getByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(macchinarioService.getByCategoria(categoria));
    }

    /**
     * Cosa fa: valida il file caricato e avvia estrazione AI dei dati macchina dal manuale PDF.
     * Perché esiste: riduce inserimento manuale dati e accelera onboarding macchinari.
     * Chi lo chiama: endpoint `POST /api/macchinari/manual/extract`; test principali:
     * `PdfUploadMachineExtractionIntegrationTest.pdf_upload_allows_machine_data_extraction_via_manual_endpoint -> MacchinarioController.extractFromManual`
     * e `FileUploadUnsupportedFormatsIntegrationTest.manual_extract_rejects_non_pdf_files_even_without_content_type -> MacchinarioController.extractFromManual`.
     * Input/Output: input `MultipartFile`; output `MachineManualExtractResponse` o errore JSON.
     * Eccezioni/edge case: file vuoto/non PDF => 400; `IOException` lettura => 500; runtime parsing => 400.
     * Side effects: chiamata LLM e parsing documento via `MachineManualExtractionService.extractFromManual`.
     *
     * @param file manuale da analizzare.
     * @return dati estratti o errore.
     */
    @PostMapping("/manual/extract")
    public ResponseEntity<?> extractFromManual(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Il file è vuoto"));
        }

        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        boolean looksLikePdf = originalName.toLowerCase().endsWith(".pdf");

        if (!looksLikePdf && !MediaType.APPLICATION_PDF_VALUE.equals(contentType)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo file PDF sono accettati"));
        }

        try {
            return ResponseEntity.ok(machineManualExtractionService.extractFromManual(file));
        } catch (IOException e) {
            log.error("Error reading manual PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la lettura del PDF: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error extracting machine data from manual: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // Assessment endpoints
    // ==========================================

    /**
     * Cosa fa: genera un nuovo assessment AI per il macchinario indicato.
     * Perché esiste: produce valutazione di conformità aggiornata su richiesta utente.
     * Chi lo chiama: endpoint `POST /api/macchinari/{id}/assessment`; test chiamante:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> MacchinarioController.generateAssessment`.
     * Input/Output: input ID macchinario; output assessment mappato in formato API o errore.
     * Eccezioni/edge case: macchinario assente o errore LLM/repository => 400.
     * Side effects: lettura DB + chiamata AI + salvataggio assessment tramite `AssessmentService.generateAssessment`.
     *
     * @param id ID del macchinario da valutare.
     * @return risultato assessment o errore.
     */
    @PostMapping("/{id}/assessment")
    public ResponseEntity<?> generateAssessment(@PathVariable Long id) {
        try {
            log.info("Generating assessment for macchinario ID: {}", id);
            AssessmentResult assessment = assessmentService.generateAssessment(id);
            return ResponseEntity.ok(mapAssessmentToResponse(assessment));
        } catch (RuntimeException e) {
            log.error("Error generating assessment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cosa fa: recupera lo storico assessment del macchinario e lo converte in formato API.
     * Perché esiste: permette confronto versioni/andamento delle valutazioni nel tempo.
     * Chi lo chiama: endpoint `GET /api/macchinari/{id}/assessments`; test chiamante:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> MacchinarioController.getAssessments`.
     * Input/Output: input ID macchinario; output lista assessment (eventualmente vuota).
     * Eccezioni/edge case: se non ci sono risultati restituisce array vuoto.
     * Side effects: nessuno, sola lettura tramite `AssessmentService.getAssessmentsByMacchinario`.
     *
     * @param id ID macchinario.
     * @return storico assessment in risposta HTTP.
     */
    @GetMapping("/{id}/assessments")
    public ResponseEntity<List<Map<String, Object>>> getAssessments(@PathVariable Long id) {
        List<AssessmentResult> assessments = assessmentService.getAssessmentsByMacchinario(id);
        List<Map<String, Object>> response = assessments.stream()
                .map(this::mapAssessmentToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Cosa fa: genera il PDF dell'ultimo assessment disponibile e lo rende scaricabile.
     * Perché esiste: fornisce un report condivisibile/archiviabile fuori dalla web app.
     * Chi lo chiama: endpoint `GET /api/macchinari/{id}/assessments/latest/pdf`; test chiamante:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> MacchinarioController.downloadLatestAssessmentPdf`.
     * Input/Output: input ID macchinario; output byte[] PDF con header `Content-Disposition`.
     * Eccezioni/edge case: macchinario o assessment assenti => 404; errori PDF generation => 500.
     * Side effects: lettura DB e generazione documento in memoria tramite `AssessmentPdfService.generateAssessmentPdf`.
     *
     * @param id ID macchinario.
     * @return payload PDF o errore JSON.
     */
    @GetMapping("/{id}/assessments/latest/pdf")
    public ResponseEntity<?> downloadLatestAssessmentPdf(@PathVariable Long id) {
        Macchinario macchinario;
        try {
            macchinario = macchinarioService.getById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }

        List<AssessmentResult> assessments = assessmentService.getAssessmentsByMacchinario(id);
        if (assessments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Nessun assessment disponibile per questo macchinario"));
        }

        AssessmentResult latest = assessments.get(0);

        try {
            byte[] pdf = assessmentPdfService.generateAssessmentPdf(macchinario, latest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setCacheControl(CacheControl.noStore());
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(assessmentPdfService.buildDownloadFilename(macchinario, latest), StandardCharsets.UTF_8)
                            .build()
            );

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error generating assessment PDF for macchinario {} (assessment {})", id, latest.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cosa fa: converte un `AssessmentResult` in mappa JSON pronta per il frontend.
     * Perché esiste: riduce accoppiamento tra entità JPA e contratto API pubblico.
     * Chi lo chiama: `MacchinarioController.generateAssessment -> MacchinarioController.mapAssessmentToResponse`
     * e `MacchinarioController.getAssessments -> MacchinarioController.mapAssessmentToResponse`.
     * Input/Output: input assessment entità; output mappa con soli campi utili alla UI.
     * Eccezioni/edge case: se assessment nullo i getter provocherebbero `NullPointerException`.
     * Side effects: nessuno, trasformazione in memoria.
     *
     * @param assessment assessment da esporre via API.
     * @return mappa dati serializzabile.
     */
    private Map<String, Object> mapAssessmentToResponse(AssessmentResult assessment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", assessment.getId());
        map.put("macchinarioId", assessment.getMacchinario().getId());
        map.put("macchinarioNome", assessment.getMacchinario().getNome());
        map.put("risultato", assessment.getRisultato());
        map.put("riepilogoConformita", assessment.getRiepilogoConformita());
        map.put("nonConformitaRilevate", assessment.getNonConformitaRilevate());
        map.put("raccomandazioni", assessment.getRaccomandazioni());
        map.put("livelloRischio", assessment.getLivelloRischio());
        map.put("punteggioConformita", assessment.getPunteggioConformita());
        map.put("documentiUtilizzati", assessment.getDocumentiUtilizzati());
        map.put("dataAssessment", assessment.getDataAssessment());
        map.put("versione", assessment.getVersione());
        return map;
    }
}
