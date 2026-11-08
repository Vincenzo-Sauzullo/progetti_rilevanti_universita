package controller.parameters;

import controller.exceptions.ParameterException;
import entity.Musicista;
import entity.TipologiaEvento;

import java.time.LocalDate;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class EventoParameters implements Parameter {
    private final LocalDate data;
    private final String luogo;
    private final TipologiaEvento tipologia;
    private final Set<Musicista> partecipanti;

    public EventoParameters(
        LocalDate data,
        String luogo,
        TipologiaEvento tipologia,
        Set<Musicista> partecipanti
    ) {
        this.data = data;
        this.luogo = luogo;
        this.tipologia = tipologia;
        this.partecipanti = partecipanti;
    }

    @Override
    public void validate() throws ParameterException {
        if(this.partecipanti.isEmpty())
            throw new ParameterException("Lista dei partecipanti non può essere vuota");
        if(!this.tipologia.verifica(partecipanti))
            throw new ParameterException("Lista dei partecipanti incompatibile con la tipologia di evento");
        if(this.data.isBefore(LocalDate.now()))
            throw new ParameterException("Data del passato");
        if(this.luogo.isEmpty())
            throw new ParameterException("Luogo vuoto");
    }

    public LocalDate getData() {
        return data;
    }

    public String getLuogo() {
        return luogo;
    }

    public TipologiaEvento getTipologia() {
        return tipologia;
    }

    public Set<Musicista> getPartecipanti() {
        return partecipanti;
    }
}
