package entity;

import controller.parameters.StrumentistaParameter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class Strumentista extends Musicista {
    private final List<Strumento> strumenti;

    public Strumentista(
        long id,
        String nome,
        String cognome,
        String email,
        String password,
        String telefono,
        int cachetPerSerata,
        List<LocalDate> disponibilita,
        List<Strumento> strumenti
    ) {
        super(id, nome, cognome, email, password, telefono, cachetPerSerata, disponibilita);
        this.strumenti = strumenti;
    }

    public Strumentista(StrumentistaParameter strumentistaParameter) {
        this(
            0,
            strumentistaParameter.getNome(),
            strumentistaParameter.getCognome(),
            strumentistaParameter.getEmail(),
            strumentistaParameter.getPassword(),
            strumentistaParameter.getTelefono(),
            strumentistaParameter.getCachetPerSerata(),
            strumentistaParameter.getDisponibilita(),
            strumentistaParameter.getStrumenti()
        );
    }

    public List<Strumento> getStrumenti() {
        return Collections.unmodifiableList(strumenti);
    }
}
