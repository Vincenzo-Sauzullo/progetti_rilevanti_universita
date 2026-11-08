package controller.parameters;

import controller.exceptions.ParameterException;
import entity.Strumento;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class StrumentistaParameter extends MusicistaParameter {
    private final List<Strumento> strumenti;

    public StrumentistaParameter(
        String nome,
        String cognome,
        String email,
        String password,
        String telefono,
        int cachetPerSerata,
        List<LocalDate> disponibilita,
        List<Strumento> strumenti
    ) {
        super(nome, cognome, email, password, telefono, cachetPerSerata, disponibilita);
        this.strumenti = strumenti;
    }

    @Override
    public void validate() throws ParameterException {
        super.validate();

        if(strumenti == null)     throw new ParameterException("Lista strumenti nulla");
        if(strumenti.isEmpty())   throw new ParameterException("Lista strumenti vuota");
        if(strumenti.size() > 4)  throw new ParameterException("Lista strumenti superiore al limite massimo");
    }

    public List<Strumento> getStrumenti() {
        return Collections.unmodifiableList(strumenti);
    }
}
