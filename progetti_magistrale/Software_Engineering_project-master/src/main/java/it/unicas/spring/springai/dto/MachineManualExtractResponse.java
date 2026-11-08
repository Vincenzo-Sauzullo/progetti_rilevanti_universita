package it.unicas.spring.springai.dto;

/**
 * DTO di output dell'estrazione dati da manuale PDF di un macchinario.
 * Ruolo architetturale: fornisce al frontend un payload strutturato pronto
 * per precompilare il form macchina dopo elaborazione AI.
 * Interagisce con {@code MachineManualExtractionService.extractFromManual}
 * e con {@code MacchinarioController.extractFromManual}.
 *
 * @param nome nome macchina ricavato dal manuale o dal nome file.
 * @param produttore produttore rilevato nel documento.
 * @param modello modello commerciale/tecnico individuato.
 * @param numeroSerie seriale o identificativo macchina, se trovato.
 * @param annoProduzione anno di produzione stimato.
 * @param categoria categoria normalizzata tra quelle supportate dall'app.
 * @param descrizione descrizione breve per contesto funzionale.
 * @param specificheTecniche estratto sintetico delle specifiche principali.
 */
public record MachineManualExtractResponse(
        String nome,
        String produttore,
        String modello,
        String numeroSerie,
        Integer annoProduzione,
        String categoria,
        String descrizione,
        String specificheTecniche
) {
}
