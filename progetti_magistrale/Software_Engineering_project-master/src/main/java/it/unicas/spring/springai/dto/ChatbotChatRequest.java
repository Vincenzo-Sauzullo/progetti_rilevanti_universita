package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO di input per la chat generale basata su RAG documentale.
 * Ruolo architetturale: ponte tra payload HTTP del frontend e logica applicativa
 * del chatbot, includendo domanda, cronologia e override del prompt di sistema.
 * Interagisce con {@code ChatbotController.chat} e {@code ChatbotService.chat}.
 *
 * @param question domanda corrente posta dall'utente.
 * @param history eventuale cronologia turni per mantenere continuità nella risposta.
 * @param systemPrompt istruzioni opzionali avanzate che possono sostituire il prompt di default.
 */
public record ChatbotChatRequest(
        String question,
        List<ChatTurn> history,
        String systemPrompt
) {
}
