package it.unicas.spring.springai.dto;

/**
 * DTO che descrive una singola fonte usata dal chatbot RAG.
 * Ruolo architetturale: rende spiegabile la risposta associando riferimento,
 * pagina e frammento testuale proveniente dai documenti indicizzati.
 * Interagisce con {@code ChatbotService.buildSources} e con la UI client.
 *
 * @param reference etichetta leggibile della fonte (es. file + pagina).
 * @param fileName nome del documento sorgente.
 * @param page pagina da cui proviene il chunk, se disponibile.
 * @param chunk estratto testuale mostrato come prova contestuale.
 * @param confidence punteggio di pertinenza associato al recupero del chunk.
 */
public record ChatbotSource(
        String reference,
        String fileName,
        Integer page,
        String chunk,
        Double confidence
) {
}
