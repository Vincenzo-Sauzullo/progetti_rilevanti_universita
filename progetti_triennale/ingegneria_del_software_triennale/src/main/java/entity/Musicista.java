package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


public abstract class Musicista {
    private long id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;
    private int cachetPerSerata;
    private List<LocalDate> disponibilita;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getCachetPerSerata() {
        return cachetPerSerata;
    }

    public void setCachetPerSerata(int cachetPerSerata) {
        this.cachetPerSerata = cachetPerSerata;
    }

    public List<LocalDate> getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(List<LocalDate> disponibilita) {
        this.disponibilita = disponibilita;
    }

    protected Musicista(long id, String nome, String cognome, String email, String password, String telefono, int cachetPerSerata, List<LocalDate> disponibilita) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.cachetPerSerata = cachetPerSerata;
        this.disponibilita = disponibilita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Musicista musicista)) return false;
        return id == musicista.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "[%3d] %-28s cachet:%-4d tel:%-10s email:%s".formatted(id, nome + " " + cognome, cachetPerSerata, telefono, email);
    }
}
