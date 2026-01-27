package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {
    private static NoteDAO instance = null;

    private NoteDAO() {}

    public static NoteDAO getInstance() {
        if (instance == null) instance = new NoteDAO();
        return instance;
    }

    public void createNote(String seller, String text, int adId) throws DAOException {
        String sql = "{call crea_nota(?, ?, ?, ?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, seller);
                cs.setString(2, text);
                cs.setInt(3, adId);
                cs.registerOutParameter(4, Types.INTEGER); // Parametro di output

                cs.execute();
            }
        } catch (SQLException e) {
            // Gestione specifica dell'errore lanciato dalla procedura
            if ("45000".equals(e.getSQLState())) {
                throw new DAOException("Operazione non autorizzata: Non sei il proprietario.");
            }
            throw new DAOException("Errore creazione nota: " + e.getMessage());
        }
    }

    public List<NoteBean> retrieveNotes(int adId) throws DAOException {
        List<NoteBean> notes = new ArrayList<>();
        String sql = "{call visualizza_note(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, adId);

                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            notes.add(new NoteBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Testo"),
                                    rs.getInt("Annuncio_Codice")
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero note: " + e.getMessage());
        }
        return notes;
    }
}