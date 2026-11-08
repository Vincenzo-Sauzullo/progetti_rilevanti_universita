package it.unicas.spring.springai.dto;

/**
 * DTO di output per la chat su assessment.
 * Ruolo architetturale: incapsula la risposta testuale prodotta dal layer AI/service
 * prima della serializzazione JSON verso frontend.
 * Interagisce con {@code AssessmentController.chatOnAssessment}, che lo costruisce
 * dopo la chiamata a {@code AssessmentChatService.chat}.
 *
 * @param answer risposta finale restituita all'utente nella UI risultati assessment.
 */
public record AssessmentChatResponse(
        String answer
) {
}
