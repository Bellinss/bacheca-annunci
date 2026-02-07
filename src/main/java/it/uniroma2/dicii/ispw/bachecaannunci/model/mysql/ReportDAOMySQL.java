package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.ReportDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;
import java.sql.*;

public class ReportDAOMySQL implements ReportDAO {
    private static ReportDAOMySQL instance = null;

    private ReportDAOMySQL() {}

    public static ReportDAOMySQL getInstance() {
        if (instance == null) {
            instance = new ReportDAOMySQL();
        }
        return instance;
    }

    @Override
    public ReportBean generateReport(String targetUsername) throws DAOException {
        // Query per contare il totale degli annunci pubblicati dall'utente
        String sqlTotal = "SELECT COUNT(*) AS totali FROM annunci WHERE venditore = ?";
        // Query per contare solo gli annunci che sono stati effettivamente venduti
        String sqlSold = "SELECT COUNT(*) AS venduti FROM annunci WHERE venditore = ? AND stato = 'venduto'";

        try {
            Connection conn = ConnectionFactory.getConnection();

            int annunciTotali = 0;
            int annunciVenduti = 0;

            // 1. Calcolo Annunci Totali
            try (PreparedStatement psTotal = conn.prepareStatement(sqlTotal)) {
                psTotal.setString(1, targetUsername);
                try (ResultSet rs = psTotal.executeQuery()) {
                    if (rs.next()) {
                        annunciTotali = rs.getInt("totali");
                    }
                }
            }

            if (annunciTotali == 0) {
                throw new DAOException("L'utente non è un venditore.");
            }

            // 2. Calcolo Annunci Venduti
            try (PreparedStatement psSold = conn.prepareStatement(sqlSold)) {
                psSold.setString(1, targetUsername);
                try (ResultSet rs = psSold.executeQuery()) {
                    if (rs.next()) {
                        annunciVenduti = rs.getInt("venduti");
                    }
                }
            }

            if (annunciVenduti == 0) {
                throw new DAOException("Nessun annuncio venduto per questo utente.");
            }

            // 3. Calcolo Percentuale e Data in Java
            float percentuale = ((float) annunciVenduti / annunciTotali) * 100;
            java.sql.Date dataOggi = new java.sql.Date(System.currentTimeMillis());

            // 4. Creazione e ritorno del Bean popolato con i dati calcolati
            return new ReportBean(
                    targetUsername,
                    dataOggi,
                    percentuale,
                    annunciTotali,
                    annunciVenduti
            );

        } catch (SQLException e) {
            throw new DAOException("Errore generazione report nel DAO: " + e.getMessage());
        }
    }
}