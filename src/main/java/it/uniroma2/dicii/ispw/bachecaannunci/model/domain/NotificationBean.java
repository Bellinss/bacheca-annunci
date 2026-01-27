package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Timestamp; // Usa Timestamp per avere data e ora
import java.text.SimpleDateFormat;

public class NotificationBean {
    private int codice;
    private String username;
    private Timestamp data;
    private String testo;

    public NotificationBean(int codice, String username, Timestamp data, String testo) {
        this.codice = codice;
        this.username = username;
        this.data = data;
        this.testo = testo;
    }

    @Override
    public String toString() {
        // Formattazione: GG/MM/AAAA
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataFormattata = (data != null) ? sdf.format(data) : "N/D";
        return "[" + dataFormattata + "] " + testo;
    }
}