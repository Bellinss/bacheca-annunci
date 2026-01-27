package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAOMySQL implements NoteDAO {
    private static NoteDAOMySQL instance = null;

    private NoteDAOMySQL() {}

    public static NoteDAOMySQL getInstance() {
        if (instance == null) instance = new NoteDAOMySQL();
        return instance;
    }

    @Override
    public void createNote(String seller, String text, int adId) throws DAOException {
        String sql = "{call crea_nota(?, ?, ?, ?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, seller);
                cs.setString(2, text);
                cs.setInt(3, adId);
                cs.registerOutParameter(4, Types.INTEGER);

                cs.execute();
            }
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                throw new DAOException("Operazione non autorizzata: Non sei il proprietario.");
            }
            throw new DAOException("Errore creazione nota: " + e.getMessage());
        }
    }

    @Override
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