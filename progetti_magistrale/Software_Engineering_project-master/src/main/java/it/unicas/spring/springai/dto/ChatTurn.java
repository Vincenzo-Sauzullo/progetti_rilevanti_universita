package it.unicas.spring.springai.dto;

/**
 * Rappresenta un singolo turno di conversazione (utente o assistente).
 * Ruolo architetturale: unità base della history usata dai service chat
 * per ricostruire il contesto conversazionale senza persistere stato server-side.
 * Interagisce con {@code ChatbotChatRequest} e {@code AssessmentChatRequest}.
 *
 * @param role ruolo del parlante (tipicamente "user" o "assistant").
 * @param content testo del messaggio associato al turno.
 */
public record ChatTurn(
        String role,
        String content
) {
}
