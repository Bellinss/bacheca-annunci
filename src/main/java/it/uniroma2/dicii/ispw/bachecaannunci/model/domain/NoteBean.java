package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.io.Serializable;

public class NoteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String testo;
    private int idAnnuncio;

    public NoteBean(int id, String testo, int idAnnuncio) {
        this.id = id;
        this.testo = testo;
        this.idAnnuncio = idAnnuncio;
    }

    // --- GETTERS AGGIUNTI PER LA DEMO VERSION ---

    public int getId() {
        return id;
    }

    public int getIdAnnuncio() {
        return idAnnuncio;
    }

    public String getTesto() {
        return testo;
    }

    @Override
    public String toString() {
        return testo;
    }
}