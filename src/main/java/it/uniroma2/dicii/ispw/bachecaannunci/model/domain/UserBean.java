package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.time.LocalDate;

public class UserBean {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String residenza;
    private String fatturazione;
    private String tipoRecapito; // "email" o "cellulare"
    private String recapito;

    public UserBean(String username, String password, String nome, String cognome,
                    LocalDate dataNascita, String residenza, String fatturazione,
                    String tipoRecapito, String recapito) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.residenza = residenza;
        this.fatturazione = fatturazione;
        this.tipoRecapito = tipoRecapito;
        this.recapito = recapito;
    }

    // Getter
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public LocalDate getDataNascita() { return dataNascita; }
    public String getResidenza() { return residenza; }
    public String getFatturazione() { return fatturazione; }
    public String getTipoRecapito() { return tipoRecapito; }
    public String getRecapito() { return recapito; }
}