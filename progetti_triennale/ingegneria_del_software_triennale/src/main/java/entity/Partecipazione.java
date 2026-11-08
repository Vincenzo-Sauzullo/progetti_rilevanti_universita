package entity;

import java.util.Objects;

public class Partecipazione {
    private Musicista musicista;
    private Evento evento;
    private int costo;
    private boolean confermato;

    public Partecipazione(Musicista musicista, Evento evento, int costo, boolean confermato) {
        this.musicista = musicista;
        this.evento = evento;
        this.costo = costo;
        this.confermato = confermato;
    }

    public Musicista getMusicista() {
        return musicista;
    }

    public void setMusicista(Musicista musicista) {
        this.musicista = musicista;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public void setConfermato(boolean confermato) {
        this.confermato = confermato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partecipazione that)) return false;
        return Objects.equals(musicista, that.musicista) && Objects.equals(evento, that.evento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicista, evento);
    }
}
