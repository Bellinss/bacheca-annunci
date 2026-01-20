package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;
import java.sql.*;

public class ReportDAO {
    private static ReportDAO instance = null;
    private ReportDAO() {}
    public static ReportDAO getInstance() {
        if (instance == null) instance = new ReportDAO();
        return instance;
    }

    public ReportBean generateReport(String targetUsername) throws DAOException {
        // La procedura genera_report restituisce un ResultSet con i dati
        String sql = "{call genera_report(?)}";
        ReportBean report = null;

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, targetUsername);

                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs.next()) {
                            // Leggo le colonne restituite dalla tabella temporanea Report
                            report = new ReportBean(
                                    rs.getString("Utente"),
                                    rs.getDate("Data"),
                                    rs.getFloat("Percentuale"),
                                    rs.getInt("Annunci totali"),
                                    rs.getInt("Annunci venduti")
                            );
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Gestione errori specifici della procedura (es. Utente non venditore)
            String state = e.getSQLState();
            if ("45003".equals(state)) throw new DAOException("L'utente non è un venditore.");
            if ("45004".equals(state)) throw new DAOException("Nessun annuncio venduto per questo utente.");

            throw new DAOException("Errore generazione report: " + e.getMessage());
        }
        return report;
    }
}