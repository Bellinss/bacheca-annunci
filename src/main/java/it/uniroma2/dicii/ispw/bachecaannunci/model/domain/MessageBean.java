package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Date;
import java.sql.Time;
import java.io.Serializable;

public class MessageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String testo;
    private Date data;
    private Time ora;
    private String mittente;
    private String recipient; // Questo campo c'era ma non veniva usato

    // Costruttore 1: Usato dal DAO MySQL (mantiene retrocompatibilità)
    public MessageBean(String testo, Date data, Time ora, String mittente) {
        this.mittente = mittente;
        this.testo = testo;
        this.data = data;
        this.ora = ora;
    }

    // Costruttore 2: Usato dal DAO FileSystem (più comodo per settare tutto subito)
    public MessageBean(String testo, Date data, Time ora, String mittente, String recipient) {
        this(testo, data, ora, mittente); // Richiama il costruttore base
        this.recipient = recipient;
    }

    // --- GETTERS ESISTENTI ---
    public String getTesto() { return testo; }
    public Date getData() { return data; }
    public Time getOra() { return ora; }
    public String getMittente() { return mittente; }

    // --- NUOVI METODI PER LA DEMO VERSION ---

    // Alias in inglese (se nel DAO usi .getSender())
    public String getSender() { return mittente; }

    // Getter per il destinatario (necessario per filtrare i messaggi nel file system)
    public String getRecipient() { return recipient; }

    // Setter per il destinatario
    public void setRecipient(String recipient) { this.recipient = recipient; }

    @Override
    public String toString() {
        return mittente + ": " + testo;
    }
}