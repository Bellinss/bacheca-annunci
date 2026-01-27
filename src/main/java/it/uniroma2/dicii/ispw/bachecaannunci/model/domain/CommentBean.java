package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

public class CommentBean {
    private int id;
    private String testo;
    private int idAnnuncio;

    public CommentBean(int id, String testo, int idAnnuncio) {
        this.id = id;
        this.testo = testo;
        this.idAnnuncio = idAnnuncio;
    }

    // Getter
    public int getId() { return id; }
    public String getTesto() { return testo; }
    public int getIdAnnuncio() { return idAnnuncio; }

    @Override
    public String toString() {
        return testo; // Utile per la ListView semplice
    }
}