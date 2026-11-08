package entity;

import controller.parameters.CantanteParameter;

import java.time.LocalDate;
import java.util.List;

public class Cantante extends Musicista {
    private GenereMusicale genereMusicale;
    private RegistroVocale registroVocale;

    public Cantante(
        long id,
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
        super(id, nome, cognome, email, password, telefono, cachetPerSerata, disponibilita);
        this.genereMusicale = genereMusicale;
        this.registroVocale = registroVocale;
    }

    public Cantante(CantanteParameter cantanteParameter) {
        this(
            0,
            cantanteParameter.getNome(),
            cantanteParameter.getCognome(),
            cantanteParameter.getEmail(),
            cantanteParameter.getPassword(),
            cantanteParameter.getTelefono(),
            cantanteParameter.getCachetPerSerata(),
            cantanteParameter.getDisponibilita(),
            cantanteParameter.getGenereMusicale(),
            cantanteParameter.getRegistroVocale()
        );
    }

    public GenereMusicale getGenereMusicale() {
        return genereMusicale;
    }

    public void setGenereMusicale(GenereMusicale genereMusicale) {
        this.genereMusicale = genereMusicale;
    }

    public RegistroVocale getRegistroVocale() {
        return registroVocale;
    }

    public void setRegistroVocale(RegistroVocale registroVocale) {
        this.registroVocale = registroVocale;
    }
}
