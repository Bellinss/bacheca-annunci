package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

public class AnnuncioBean {
    private int codice;
    private String titolo;
    private double importo;
    private String descrizione;
    private String venditore;
    private String categoria;

    public AnnuncioBean(int codice, String titolo, double importo, String descrizione, String venditore, String categoria) {
        this.codice = codice;
        this.titolo = titolo;
        this.importo = importo;
        this.descrizione = descrizione;
        this.venditore = venditore;
        this.categoria = categoria;
    }

    // --- Getter e Setter ---

    public int getId() { return codice; }
    public void setCodice(int codice) { this.codice = codice; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public double getImporto() { return importo; }
    public void setImporto(double importo) { this.importo = importo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getVenditore() { return venditore; }
    public void setVenditore(String venditore) { this.venditore = venditore; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}