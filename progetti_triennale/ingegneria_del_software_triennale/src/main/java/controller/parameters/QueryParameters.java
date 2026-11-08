package controller.parameters;

import controller.exceptions.ParameterException;
import entity.GenereMusicale;
import entity.RegistroVocale;
import entity.Strumento;

import java.time.LocalDate;
import java.util.List;

public class QueryParameters implements Parameter {
    private String query;
    private boolean cantanti;
    private boolean strumentisti;
    private List<GenereMusicale> generiMusicali;
    private List<RegistroVocale> registriVocali;
    private List<Strumento> strumenti;
    private LocalDate disponibileDa;
    private LocalDate disponibileA;

    public QueryParameters(
        String query,
        boolean cantanti,
        boolean strumentisti,
        List<GenereMusicale> generiMusicali,
        List<RegistroVocale> registriVocali,
        List<Strumento> strumenti,
        LocalDate disponibileDa,
        LocalDate disponibileA
    ) {
        this.query = query;
        this.cantanti = cantanti;
        this.strumentisti = strumentisti;
        this.generiMusicali = generiMusicali;
        this.registriVocali = registriVocali;
        this.strumenti = strumenti;
        this.disponibileDa = disponibileDa;
        this.disponibileA = disponibileA;
    }

    public QueryParameters(String query) {
        this(query, true, true, null, null, null, null, null);
    }

    public QueryParameters() {
        this("", true, true, null, null, null, null, null);
    }

    @Override
    public void validate() throws ParameterException {
        if(!cantanti && !strumentisti)
            throw new ParameterException("Sono esclusi sia i cantanti che gli strumentisti");
        if(disponibileDa != null && disponibileA != null && disponibileDa.isAfter(disponibileA))
            throw new ParameterException("Data di inizio futura alla data di fine");
    }

    public String getQuery() {
        return query;
    }

    public boolean cantanti() {
        return cantanti;
    }

    public boolean strumentisti() {
        return strumentisti;
    }

    public List<GenereMusicale> getGeneriMusicali() {
        return generiMusicali;
    }

    public List<RegistroVocale> getRegistriVocali() {
        return registriVocali;
    }

    public List<Strumento> getStrumenti() {
        return strumenti;
    }

    public LocalDate getDisponibileDa() {
        return disponibileDa;
    }

    public LocalDate getDisponibileA() {
        return disponibileA;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setCantanti(boolean cantanti) {
        this.cantanti = cantanti;
    }

    public void setStrumentisti(boolean strumentisti) {
        this.strumentisti = strumentisti;
    }

    public void setGeneriMusicali(List<GenereMusicale> generiMusicali) {
        this.generiMusicali = generiMusicali;
    }

    public void setRegistriVocali(List<RegistroVocale> registriVocali) {
        this.registriVocali = registriVocali;
    }

    public void setStrumenti(List<Strumento> strumenti) {
        this.strumenti = strumenti;
    }

    public void setDisponibileDa(LocalDate disponibileDa) {
        this.disponibileDa = disponibileDa;
    }

    public void setDisponibileA(LocalDate disponibileA) {
        this.disponibileA = disponibileA;
    }
}
