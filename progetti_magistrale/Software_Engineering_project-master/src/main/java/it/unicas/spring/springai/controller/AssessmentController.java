package it.unicas.spring.springai.controller;

import it.unicas.spring.springai.dto.AssessmentChatRequest;
import it.unicas.spring.springai.dto.AssessmentChatResponse;
import it.unicas.spring.springai.model.AssessmentResult;
import it.unicas.spring.springai.service.AssessmentChatService;
import it.unicas.spring.springai.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
/**
 * Controller REST per consultazione assessment e chat contestuale sui risultati.
 * Ruolo architetturale: punto di ingresso web per leggere valutazioni salvate
 * e delegare ai service la conversazione guidata sull'assessment selezionato.
 * Interagisce con {@link AssessmentService} (dati assessment) e
 * {@link AssessmentChatService} (domande/risposte contestuali).
 */
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final AssessmentChatService assessmentChatService;

    /**
     * Cosa fa: restituisce tutti gli assessment ordinati e già mappati in payload API.
     * Perché esiste: permette al frontend di costruire elenco storico delle valutazioni.
     * Chi lo chiama: endpoint `GET /api/assessments`; nel flusso test:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> AssessmentController.getAllAssessments`.
     * Input/Output: nessun input; output lista di mappe con campi principali assessment.
     * Eccezioni/edge case: lista vuota restituita come `[]`.
     * Side effects: nessuno, sola lettura DB via `AssessmentService.getAllAssessments`.
     *
     * @return risposta HTTP con storico assessment.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAssessments() {
        List<AssessmentResult> assessments = assessmentService.getAllAssessments();
        List<Map<String, Object>> response = assessments.stream()
                .map(this::mapAssessmentToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Cosa fa: recupera un assessment specifico e lo converte in formato API.
     * Perché esiste: supporta pagina dettaglio o ricarica puntuale del risultato.
     * Chi lo chiama: endpoint `GET /api/assessments/{id}`; test chiamante:
     * `LoginFlowIntegrationTest.user_can_login_and_reach_final_results_flow -> AssessmentController.getAssessment`.
     * Input/Output: riceve ID assessment; restituisce mappa dettaglio o 404.
     * Eccezioni/edge case: `RuntimeException` del service (ID assente) mappata in Not Found.
     * Side effects: nessun effetto collaterale, sola lettura repository.
     *
     * @param id identificativo assessment richiesto.
     * @return dettaglio assessment o 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssessment(@PathVariable Long id) {
        try {
            AssessmentResult assessment = assessmentService.getAssessment(id);
            return ResponseEntity.ok(mapAssessmentToResponse(assessment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cosa fa: inoltra una domanda sull'assessment selezionato al servizio di chat contestuale.
     * Perché esiste: consente analisi guidata post-assessment senza perdere contesto dei finding.
     * Chi lo chiama: endpoint `POST /api/assessments/{id}/chat`; test principali:
     * `AssessmentChatContextIntegrationTest.* -> AssessmentController.chatOnAssessment`.
     * Input/Output: input ID assessment + `AssessmentChatRequest`; output `AssessmentChatResponse` o errore JSON.
     * Eccezioni/edge case: input non valido => 400; assessment mancante => 404; errori runtime => 500/404.
     * Side effects: chiamata rete verso modello AI tramite `AssessmentChatService.chat`.
     *
     * @param id assessment su cui contestualizzare la domanda.
     * @param request domanda/history inviate dal client.
     * @return risposta chat oppure dettaglio errore.
     */
    @PostMapping("/{id}/chat")
    public ResponseEntity<?> chatOnAssessment(@PathVariable Long id, @RequestBody AssessmentChatRequest request) {
        try {
            String answer = assessmentChatService.chat(id, request);
            return ResponseEntity.ok(new AssessmentChatResponse(answer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : "Errore durante la chat";
            HttpStatus status = message.toLowerCase().contains("non trovato") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(Map.of("error", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la chat: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssessment(@PathVariable Long id) {
        try {
            assessmentService.deleteAssessment(id);
            return ResponseEntity.ok(Map.of("message", "Assessment eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cosa fa: converte `AssessmentResult` (entità) in una mappa semplice per API REST.
     * Perché esiste: evita di esporre direttamente l'entità JPA e stabilizza il contratto JSON.
     * Chi lo chiama: `AssessmentController.getAllAssessments -> AssessmentController.mapAssessmentToResponse`
     * e `AssessmentController.getAssessment -> AssessmentController.mapAssessmentToResponse`.
     * Input/Output: prende un assessment; restituisce mappa serializzabile con campi selezionati.
     * Eccezioni/edge case: richiede oggetto non nullo; con valore nullo si avrebbe `NullPointerException`.
     * Side effects: nessuno, trasformazione in memoria.
     *
     * @param assessment assessment di dominio da trasformare.
     * @return mappa pronta per risposta REST.
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
