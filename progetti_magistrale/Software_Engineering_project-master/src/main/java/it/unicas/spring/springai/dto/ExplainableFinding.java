package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO di un finding finale (non conformità o raccomandazione) con fonti esplicite.
 * Ruolo architetturale: oggetto di trasferimento spiegabile tra service assessment/chat
 * e serializzazione JSON persistita nel database.
 * Interagisce con {@code AssessmentService.toExplainableFindings} e
 * {@code AssessmentChatService.parseFindings}.
 *
 * @param text descrizione leggibile del finding.
 * @param sources fonti normative/documentali che supportano il finding.
 */
public record ExplainableFinding(
        String text,
        List<ExplainableSource> sources
) {
}
