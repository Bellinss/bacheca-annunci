package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Date;
import java.sql.Time;

public class MessageBean {
    private String testo;
    private Date data;
    private Time ora;
    private String mittente;

    public MessageBean(String testo, Date data, Time ora, String mittente) {
        this.mittente = mittente;
        this.testo = testo;
        this.data = data;
        this.ora = ora;
    }

    public String getTesto() { return testo; }
    public Date getData() { return data; }
    public Time getOra() { return ora; }
    public String getMittente() { return mittente; }

    @Override
    public String toString() {
        return mittente + ": " + testo;
    }
}