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
    private String destinatario;

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
        this.destinatario = recipient;
    }

    // --- GETTERS ---
    public String getTesto() { return testo; }
    public Date getData() { return data; }
    public Time getOra() { return ora; }
    public String getMittente() { return mittente; }
    public String getDestinatario() { return destinatario; }

    // Setter per il destinatario
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    @Override
    public String toString() {
        return mittente + ": " + testo;
    }
}