package it.unicas.spring.springai.dto;

import java.util.List;

/**
 * DTO che modella la risposta JSON "explainable" attesa dall'LLM per gli assessment.
 * Ruolo architetturale: schema intermedio tra output AI e entità persistita,
 * utile per mantenere separata la forma del payload dalla logica di dominio.
 * Interagisce concettualmente con {@code AssessmentService.parseExplainableAssessmentResponse}
 * (uso non evidente diretto nel codice: possibile utilizzo futuro/rifattorizzazione).
 *
 * @param riepilogoConformita sintesi dello stato di conformità.
 * @param nonConformitaRilevate elenco di finding con chunkId di supporto.
 * @param raccomandazioni elenco di azioni raccomandate con fonti.
 * @param livelloRischio livello di rischio previsto dall'assessment.
 * @param punteggioConformita punteggio normalizzato atteso tra 0 e 100.
 */
public record ExplainableAssessmentLlmResponse(
        String riepilogoConformita,
        List<ExplainableFindingDraft> nonConformitaRilevate,
        List<ExplainableFindingDraft> raccomandazioni,
        String livelloRischio,
        Integer punteggioConformita
) {
}
