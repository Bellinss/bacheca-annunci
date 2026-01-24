package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Date;

public class UserBean {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private Date dataNascita; // Cambiato da LocalDate a java.sql.Date
    private String residenza;
    private String fatturazione;
    private String tipoRecapito;
    private String recapito;

    // 1. Costruttore Vuoto (Fondamentale per i Bean)
    public UserBean() {}

    // 2. Costruttore Completo aggiornato con java.sql.Date
    public UserBean(String username, String password, String nome, String cognome,
                    Date dataNascita, String residenza, String fatturazione,
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

    // --- Getter e Setter ---

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    // Getter e Setter aggiornati per Date
    public Date getDataNascita() { return dataNascita; }
    public void setDataNascita(Date dataNascita) { this.dataNascita = dataNascita; }

    public String getResidenza() { return residenza; }
    public void setResidenza(String residenza) { this.residenza = residenza; }

    public String getFatturazione() { return fatturazione; }
    public void setFatturazione(String fatturazione) { this.fatturazione = fatturazione; }

    public String getTipoRecapito() { return tipoRecapito; }
    public void setTipoRecapito(String tipoRecapito) { this.tipoRecapito = tipoRecapito; }

    public String getRecapito() { return recapito; }
    public void setRecapito(String recapito) { this.recapito = recapito; }
}