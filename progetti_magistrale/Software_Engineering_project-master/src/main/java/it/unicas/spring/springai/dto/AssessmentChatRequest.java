package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO di input per la chat contestuale su un assessment già salvato.
 * Ruolo architetturale: trasporta dal layer HTTP (JSON) al layer service
 * la domanda dell'utente e la cronologia minima necessaria a mantenere il contesto.
 * Interagisce con {@code AssessmentController.chatOnAssessment} e con
 * {@code AssessmentChatService.chat}, che ne validano i contenuti prima di interrogare l'LLM.
 *
 * @param question domanda dell'utente sul risultato assessment selezionato.
 * @param history cronologia opzionale dei turni chat (utente/assistente) per continuità conversazionale.
 */
public record AssessmentChatRequest(
        String question,
        List<ChatTurn> history
) {
}
