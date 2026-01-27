package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class NotificationBean implements Serializable {

    private static final long serialVersionUID = 1L;

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

    // --- GETTERS AGGIUNTI PER LA LOGICA (DEMO & UI) ---

    public int getCodice() {
        return codice;
    }

    public String getUsername() {
        // Fondamentale per il filtro nel NotificationDAOFileSystem
        return username;
    }

    public Timestamp getData() {
        return data;
    }

    public String getTesto() {
        return testo;
    }

    @Override
    public String toString() {
        // Formattazione: GG/MM/AAAA
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormattata = (data != null) ? sdf.format(data) : "N/D";
        return "[" + dataFormattata + "] " + testo;
    }
}