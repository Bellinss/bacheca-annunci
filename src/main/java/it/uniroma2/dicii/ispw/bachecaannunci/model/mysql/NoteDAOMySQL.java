package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAOMySQL implements NoteDAO {

    private static NoteDAOMySQL instance = null;

    private NoteDAOMySQL() {}

    public static NoteDAOMySQL getInstance() {
        if (instance == null) {
            instance = new NoteDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. CREATE NOTE
    // --------------------------------------------------------------------------------
    @Override
    public void createNote(String seller, String text, int adId) throws DAOException {
        String checkSql = "SELECT venditore FROM annunci WHERE codice = ?";
        String insertSql = "INSERT INTO note (testo, id_annuncio) VALUES (?, ?)";

        try {
            Connection conn = ConnectionFactory.getConnection();

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, adId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String realOwner = rs.getString("venditore");
                        if (!realOwner.equals(seller)) {
                            throw new DAOException("Operazione non autorizzata: Non sei il proprietario.");
                        }
                    } else {
                        throw new DAOException("Annuncio non trovato.");
                    }
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, text);
                psInsert.setInt(2, adId);
                psInsert.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DAOException("Errore creazione nota: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 2. RETRIEVE NOTES
    // --------------------------------------------------------------------------------
    @Override
    public List<NoteBean> retrieveNotes(int adId) throws DAOException {
        List<NoteBean> notes = new ArrayList<>();

        // Query 1: Controllo proprietario
        String checkOwnerSql = "SELECT venditore FROM annunci WHERE codice = ?";
        // Query 2: Recupero note
        String getNotesSql = "SELECT * FROM note WHERE id_annuncio = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();

            // --- STEP 1: LOGICA DI AUTORIZZAZIONE IN JAVA ---
            Credentials loggedUser = Session.getInstance().getLoggedUser();

            // Se nessuno è loggato, non mostriamo note private
            if (loggedUser == null) {
                return notes; // Ritorna lista vuota
            }

            try (PreparedStatement psCheck = conn.prepareStatement(checkOwnerSql)) {
                psCheck.setInt(1, adId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String owner = rs.getString("venditore");
                        // Se l'utente loggato NON è il venditore, blocca l'accesso
                        if (!owner.equals(loggedUser.getUsername())) {
                            return notes;
                        }
                    } else {
                        // L'annuncio non esiste nemmeno
                        return notes;
                    }
                }
            }

            // --- STEP 2: RECUPERO DATI ---
            try (PreparedStatement ps = conn.prepareStatement(getNotesSql)) {
                ps.setInt(1, adId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        notes.add(new NoteBean(
                                rs.getInt("id"),
                                rs.getString("testo"),
                                rs.getInt("id_annuncio")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero note: " + e.getMessage());
        }
        return notes;
    }
}