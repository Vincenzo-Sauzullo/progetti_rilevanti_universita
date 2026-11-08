package it.unicas.spring.springai.controller;

import it.unicas.spring.springai.dto.ChatbotChatRequest;
import it.unicas.spring.springai.dto.ChatbotChatResponse;
import it.unicas.spring.springai.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
/**
 * Controller REST del chatbot generale basato su documenti indicizzati (RAG).
 * Ruolo architetturale: espone endpoint per recuperare prompt di sistema
 * e per inviare domande utente al layer service AI.
 * Interagisce direttamente con {@link ChatbotService}.
 */
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Cosa fa: restituisce il prompt di sistema standard usato dal chatbot.
     * Perché esiste: consente al frontend di mostrare/configurare le regole operative correnti.
     * Chi lo chiama: endpoint `GET /api/chatbot/system-prompt`; chiamante Java diretto non trovato.
     * Input/Output: nessun input; output mappa JSON con chiave `systemPrompt`.
     * Eccezioni/edge case: non gestisce casi errore specifici perché legge una costante applicativa.
     * Side effects: nessuno.
     *
     * @return prompt di sistema corrente in payload JSON.
     */
    @GetMapping("/system-prompt")
    public ResponseEntity<?> systemPrompt() {
        return ResponseEntity.ok(Map.of("systemPrompt", chatbotService.defaultSystemPrompt()));
    }

    /**
     * Cosa fa: valida/gestisce la richiesta chat e delega la risposta a `ChatbotService`.
     * Perché esiste: è l'endpoint principale per Q&A su normative/manuali caricati.
     * Chi lo chiama: endpoint `POST /api/chatbot/chat`; test principali:
     * `ChatbotGeneralIntegrationTest.* -> ChatbotController.chat` e
     * `ChatbotMessageEditingIntegrationTest.modifica_messaggio_inoltra_nuova_domanda_e_restituisce_risposta_rigenerata -> ChatbotController.chat`.
     * Input/Output: input `ChatbotChatRequest`; output `ChatbotChatResponse` o errore JSON.
     * Eccezioni/edge case: input non valido => 400; eccezioni runtime/checked => 500.
     * Side effects: invoca pipeline RAG+LLM (`ChatbotService.chat`) con possibili chiamate rete.
     *
     * @param request domanda utente, history opzionale e possibile prompt custom.
     * @return risposta chatbot o dettaglio errore.
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatbotChatRequest request) {
        try {
            ChatbotChatResponse response = chatbotService.chat(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : "Errore durante la chat";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore durante la chat: " + e.getMessage()));
        }
    }
}
