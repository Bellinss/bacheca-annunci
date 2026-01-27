package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

public class NoteBean {
    private int id;
    private String testo;
    private int idAnnuncio;

    public NoteBean(int id, String testo, int idAnnuncio) {
        this.id = id;
        this.testo = testo;
        this.idAnnuncio = idAnnuncio;
    }

    public String getTesto() { return testo; }

    @Override
    public String toString() {
        return testo;
    }
}