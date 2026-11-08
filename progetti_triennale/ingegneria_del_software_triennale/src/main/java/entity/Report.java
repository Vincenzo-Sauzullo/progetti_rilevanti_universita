package entity;

import java.util.List;
import java.util.Map;

public class Report {
    private final List<Evento> eventi;
    private final Map<Musicista, Integer> compensiMusicisti;

    public Report(List<Evento> eventi, Map<Musicista, Integer> compensiMusicisti) {
        this.eventi = eventi;
        this.compensiMusicisti = compensiMusicisti;
    }

    public List<Evento> getEventi() {
        return eventi;
    }

    public Map<Musicista, Integer> getCompensiMusicisti() {
        return compensiMusicisti;
    }
}
