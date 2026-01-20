package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.sql.Date;

public class ReportBean {
    private String username;
    private Date data;
    private float percentuale;
    private int annunciTotali;
    private int annunciVenduti;

    public ReportBean(String username, Date data, float percentuale, int annunciTotali, int annunciVenduti) {
        this.username = username;
        this.data = data;
        this.percentuale = percentuale;
        this.annunciTotali = annunciTotali;
        this.annunciVenduti = annunciVenduti;
    }

    // Getters
    public String getUsername() { return username; }
    public float getPercentuale() { return percentuale; }
    public int getAnnunciTotali() { return annunciTotali; }
    public int getAnnunciVenduti() { return annunciVenduti; }

    @Override
    public String toString() {
        return String.format("Utente: %s\nVenduti: %d/%d\nSuccesso: %.2f%%",
                username, annunciVenduti, annunciTotali, percentuale);
    }
}