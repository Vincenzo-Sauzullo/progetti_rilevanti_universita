package controller.exceptions;

import java.time.LocalDate;

public class EventoConcomitanteException extends Exception {
    public EventoConcomitanteException(LocalDate data, String luogo) {
        super("Esiste già un evento fissato per la data \"%s\" e per il luogo \"%s\"".formatted(data, luogo));
    }
}
