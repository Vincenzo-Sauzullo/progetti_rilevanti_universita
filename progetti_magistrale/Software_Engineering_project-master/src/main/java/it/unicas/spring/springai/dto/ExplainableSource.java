package it.unicas.spring.springai.dto;

/**
 * DTO che rappresenta una fonte di evidenza per explainability normativa.
 * Ruolo architetturale: collega ogni finding al frammento documentale che lo giustifica,
 * così il frontend e i service di chat possono mostrare citazioni verificabili.
 * Interagisce con {@code AssessmentService.toExplainableSource} e
 * {@code AssessmentChatService.buildSources}.
 *
 * @param reference riferimento sintetico (file/articolo/pagina) mostrabile all'utente.
 * @param fileName nome del documento da cui deriva il chunk.
 * @param page pagina sorgente, se disponibile nei metadati.
 * @param chunk contenuto testuale usato come prova.
 * @param confidence pertinenza associata al chunk recuperato.
 */
public record ExplainableSource(
        String reference,
        String fileName,
        Integer page,
        String chunk,
        Double confidence
) {
}
