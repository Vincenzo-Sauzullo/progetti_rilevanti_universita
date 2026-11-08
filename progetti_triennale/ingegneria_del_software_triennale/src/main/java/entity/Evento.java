package entity;

import controller.parameters.EventoParameters;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Evento {
    private long id;
    private LocalDate data;
    private String luogo;
    private int costoTotale;
    private TipologiaEvento tipologia;
    private StatoEvento stato;
    private Set<Partecipazione> partecipanti;

    public Evento(
        long id,
        LocalDate data,
        String luogo,
        int costoTotale,
        TipologiaEvento tipologia,
        StatoEvento stato,
        Set<Partecipazione> partecipanti
    ) {
        this.id = id;
        this.data = data;
        this.luogo = luogo;
        this.costoTotale = costoTotale;
        this.tipologia = tipologia;
        this.stato = stato;
        this.partecipanti = partecipanti;
    }

    public Evento(EventoParameters eventoParameters) {
        this(
            0,
            eventoParameters.getData(),
            eventoParameters.getLuogo(),
            eventoParameters.getPartecipanti().stream().mapToInt(Musicista::getCachetPerSerata).sum(),
            eventoParameters.getTipologia(),
            StatoEvento.NON_CONFERMATO,
            null
        );

        this.partecipanti = eventoParameters.getPartecipanti().stream().map(
            m -> new Partecipazione(m, this, m.getCachetPerSerata(), false)
        ).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento evento)) return false;
        return id == evento.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public int getCostoTotale() {
        return costoTotale;
    }

    public void setCostoTotale(int costoTotale) {
        this.costoTotale = costoTotale;
    }

    public TipologiaEvento getTipologia() {
        return tipologia;
    }

    public void setTipologia(TipologiaEvento tipologia) {
        this.tipologia = tipologia;
    }

    public StatoEvento getStato() {
        return stato;
    }

    public void setStato(StatoEvento stato) {
        this.stato = stato;
    }

    public Set<Partecipazione> getPartecipanti() {
        return Collections.unmodifiableSet(partecipanti);
    }

    public void setPartecipanti(Set<Partecipazione> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public String toString() {
        String musicistiStr = partecipanti.stream().map(Partecipazione::getMusicista).map(m ->
            "%s %s".formatted(m.getNome(), m.getCognome())
        ).collect(Collectors.joining(", "));

        return String.format("%3d | %-12s | %-15s | %-12d | %-10s | %-10s | %s",
                id, data.toString(), luogo, costoTotale, tipologia, stato, musicistiStr);
    }
}
