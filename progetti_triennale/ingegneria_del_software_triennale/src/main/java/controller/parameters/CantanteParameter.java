package controller.parameters;

import controller.exceptions.ParameterException;
import entity.GenereMusicale;
import entity.RegistroVocale;

import java.time.LocalDate;
import java.util.List;

public class CantanteParameter extends MusicistaParameter {
    private final GenereMusicale genereMusicale;
    private final RegistroVocale registroVocale;

    public CantanteParameter(
        String nome,
        String cognome,
        String email,
        String password,
        String telefono,
        int cachetPerSerata,
        List<LocalDate> disponibilita,
        GenereMusicale genereMusicale,
        RegistroVocale registroVocale
    ) {
        super(nome, cognome, email, password, telefono, cachetPerSerata, disponibilita);
        this.genereMusicale = genereMusicale;
        this.registroVocale = registroVocale;
    }

    @Override
    public void validate() throws ParameterException {
        super.validate();

        if(genereMusicale == null)
            throw new ParameterException("Genere musicale nullo");
        if(genereMusicale == GenereMusicale.CLASSICA && registroVocale == null)
            throw new ParameterException("Genere musicale classico richiede obbligatoriamente un registro vocale non nullo");
        if(genereMusicale != GenereMusicale.CLASSICA && registroVocale != null)
            throw new ParameterException("Registro vocale può essere specificato solo quando il genere musicale è classico");
    }

    public GenereMusicale getGenereMusicale() {
        return genereMusicale;
    }

    public RegistroVocale getRegistroVocale() {
        return registroVocale;
    }
}
