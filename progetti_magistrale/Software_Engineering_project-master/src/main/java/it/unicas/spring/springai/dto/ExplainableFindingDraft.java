package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO intermedio usato per leggere finding grezzi dall'output JSON del modello.
 * Ruolo architetturale: separa la forma minima prodotta dall'LLM dalla struttura
 * arricchita con fonti risolte che verrà poi salvata/esposta.
 * Interagisce con {@code AssessmentService.parseFindingDrafts}.
 *
 * @param text testo del finding estratto dall'LLM.
 * @param chunkIds identificativi numerici dei chunk citati dal modello.
 */
public record ExplainableFindingDraft(
        String text,
        List<Integer> chunkIds
) {
}
