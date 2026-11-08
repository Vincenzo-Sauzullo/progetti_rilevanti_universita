package controller.parameters;


import controller.exceptions.ParameterException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe immutabile astratta che gestisce il passaggio dei parametri ai fini della creazione di Cantanti e Strumentisti.
 * */
public abstract class MusicistaParameter implements Parameter {
    protected final String nome;
    protected final String cognome;
    protected final String email;
    protected final String password;
    protected final String telefono;
    protected final int cachetPerSerata;
    protected final List<LocalDate> disponibilita;

    public MusicistaParameter(
        String nome,
        String cognome,
        String email,
        String password,
        String telefono,
        int cachetPerSerata,
        List<LocalDate> disponibilita
    ) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.cachetPerSerata = cachetPerSerata;
        this.disponibilita = disponibilita == null ? null : new ArrayList<>(disponibilita);
    }

    @Override
    public void validate() throws ParameterException {
        if(nome == null)                     throw new ParameterException("Nome nullo");
        if(cognome == null)                  throw new ParameterException("Cognome nullo");
        if(email == null)                    throw new ParameterException("Email nulla");
        if(password == null)                 throw new ParameterException("Password nulla");
        if(telefono == null)                 throw new ParameterException("Telefono nullo");
        if(disponibilita == null)            throw new ParameterException("Disponibilità nulla");
        if(nome.length() > 255)              throw new ParameterException("Lunghezza nome eccessiva");
        if(cognome.length() > 255)           throw new ParameterException("Lunghezza cognome eccessiva");
        if(email.length() > 255)             throw new ParameterException("Lunghezza email eccessiva");
        if(password.length() > 255)          throw new ParameterException("Lunghezza password eccessiva");
        if(telefono.length() > 255)          throw new ParameterException("Lunghezza telefono eccessiva");
        if(cachetPerSerata < 0)              throw new ParameterException("Cachet per serata minore di zero");
        if(!email.matches("^.+?@.+?\\..+$")) throw new ParameterException("Email non valida");
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getCachetPerSerata() {
        return cachetPerSerata;
    }

    public List<LocalDate> getDisponibilita() {
        return disponibilita;
    }
}
