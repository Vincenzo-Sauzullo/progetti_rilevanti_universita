package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO di output della chat generale RAG.
 * Ruolo architetturale: consegna al frontend sia la risposta linguistica,
 * sia le fonti citate per spiegabilità e tracciabilità.
 * Interagisce con {@code ChatbotService.chat}, che lo costruisce, e con
 * {@code ChatbotController.chat}, che lo restituisce come JSON.
 *
 * @param answer testo finale visualizzato all'utente.
 * @param sources elenco delle fonti/chunk usati o suggeriti come riferimento.
 */
public record ChatbotChatResponse(
        String answer,
        List<ChatbotSource> sources
) {
}
