package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdDAOMySQL implements AdDAO {

    private static AdDAOMySQL instance = null;

    private AdDAOMySQL() {}

    public static AdDAOMySQL getInstance() {
        if (instance == null) {
            instance = new AdDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. CREATE AD: Creazione di un nuovo annuncio
    // --------------------------------------------------------------------------------
    @Override
    public void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException {
        // Imposta lo stato a 'disponibile' di default
        String sql = "INSERT INTO annunci (titolo, importo, descrizione, venditore, categoria, stato) VALUES (?, ?, ?, ?, ?, 'disponibile')";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, titolo);
                ps.setDouble(2, prezzo);
                ps.setString(3, descrizione);
                ps.setString(4, utente);
                ps.setString(5, categoria);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore creazione annuncio: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 2. FIND ALL: Restituisce tutti gli annunci (filtrati per 'disponibile')
    // --------------------------------------------------------------------------------
    @Override
    public List<AnnuncioBean> findAll() throws DAOException {
        List<AnnuncioBean> annunci = new ArrayList<>();
        // Logica di Business: mostriamo solo quelli disponibili
        String sql = "SELECT * FROM annunci WHERE stato = 'disponibile'";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        annunci.add(mapRowToBean(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura annunci: " + e.getMessage());
        }
        return annunci;
    }

    // --------------------------------------------------------------------------------
    // 3. SEGUI ANNUNCIO: Inserimento nella tabella 'segue'
    // --------------------------------------------------------------------------------
    @Override
    public boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException {
        // Necessaria la tabella 'segue' nel DB (Utente_Username, Annuncio_Codice)
        String sql = "INSERT INTO segue (Utente_Username, Annuncio_Codice) VALUES (?, ?)";

        try {
            Connection conn = ConnectionFactory.getConnection();
            // Verifica sessione (logica mantenuta dal vecchio DAO)
            if (Session.getInstance().getLoggedUser() == null) {
                throw new DAOException("Utente non loggato.");
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setInt(2, idAnnuncio);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            // Gestione duplicati (già seguito)
            if (e.getErrorCode() == 1062 || e.getSQLState().startsWith("23")) {
                return false;
            }
            throw new DAOException("Errore nel seguire annuncio: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 4. INSERT: Variante di creazione che usa la Sessione e ritorna boolean
    // --------------------------------------------------------------------------------
    @Override
    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        String sql = "INSERT INTO annunci (titolo, importo, descrizione, venditore, categoria, stato) VALUES (?, ?, ?, ?, ?, 'disponibile')";

        try {
            Connection conn = ConnectionFactory.getConnection();
            if (conn == null) throw new DAOException("Connessione assente");

            Credentials user = Session.getInstance().getLoggedUser();
            if (user == null) throw new DAOException("Nessun utente loggato nella sessione!");

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, titolo);
                ps.setDouble(2, importo);
                ps.setString(3, descrizione);
                ps.setString(4, user.getUsername());
                ps.setString(5, categoria);

                int rows = ps.executeUpdate();
                // Verifica se è stato generato un ID (successo)
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        return rs.next(); // Ritorna true se c'è un ID generato
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore pubblicazione annuncio: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 5. FIND BY STRING: Ricerca per titolo o descrizione
    // --------------------------------------------------------------------------------
    @Override
    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        // Cerca testo nel titolo o descrizione, solo annunci disponibili
        String sql = "SELECT * FROM annunci WHERE (titolo LIKE ? OR descrizione LIKE ?) AND stato = 'disponibile'";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String pattern = "%" + queryText + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        risultati.add(mapRowToBean(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nella ricerca: " + e.getMessage());
        }
        return risultati;
    }

    // --------------------------------------------------------------------------------
    // 6. IS FOLLOWING: Controlla se l'utente segue l'annuncio
    // --------------------------------------------------------------------------------
    @Override
    public boolean isFollowing(String username, int idAnnuncio) throws DAOException {
        String sql = "SELECT 1 FROM segue WHERE Utente_Username = ? AND Annuncio_Codice = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setInt(2, idAnnuncio);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore verifica follow: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 7. MARK AS SOLD: Segna come venduto
    // --------------------------------------------------------------------------------
    @Override
    public void markAsSold(int idAnnuncio, String venditore) throws DAOException {
        // 1. Verifica Proprietario (Logica di Business in Java)
        String checkSql = "SELECT venditore FROM annunci WHERE codice = ?";
        String updateSql = "UPDATE annunci SET stato = 'venduto' WHERE codice = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();

            // Check ownership
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, idAnnuncio);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String realVendor = rs.getString("venditore");
                        if (!realVendor.equals(venditore)) {
                            throw new DAOException("Non sei il proprietario di questo annuncio.");
                        }
                    } else {
                        throw new DAOException("Annuncio non trovato.");
                    }
                }
            }

            // Execute Update
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, idAnnuncio);
                psUpdate.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel segnare come venduto: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 8. FIND BY CATEGORY: Filtra per categoria
    // --------------------------------------------------------------------------------
    @Override
    public List<AnnuncioBean> findByCategory(String categoria) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        String sql = "SELECT * FROM annunci WHERE categoria = ? AND stato = 'disponibile'";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, categoria);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        risultati.add(mapRowToBean(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore filtro categoria: " + e.getMessage());
        }
        return risultati;
    }

    // --------------------------------------------------------------------------------
    // 9. FIND FOLLOWED ADS: Annunci seguiti dall'utente
    // --------------------------------------------------------------------------------
    @Override
    public List<AnnuncioBean> findFollowedAds(String username) throws DAOException {
        List<AnnuncioBean> annunci = new ArrayList<>();
        // Join tra annunci e segue
        String sql = "SELECT a.* FROM annunci a " +
                "JOIN segue s ON a.codice = s.Annuncio_Codice " +
                "WHERE s.Utente_Username = ? AND a.stato = 'disponibile'";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        annunci.add(mapRowToBean(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero annunci seguiti: " + e.getMessage());
        }
        return annunci;
    }

    // --------------------------------------------------------------------------------
    // 10. FIND FOLLOWED BY CATEGORY: Seguiti + Filtro Categoria
    // --------------------------------------------------------------------------------
    @Override
    public List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        String sql = "SELECT a.* FROM annunci a " +
                "JOIN segue s ON a.codice = s.Annuncio_Codice " +
                "WHERE s.Utente_Username = ? AND a.categoria = ? AND a.stato = 'disponibile'";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, categoria);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        risultati.add(mapRowToBean(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore filtro combinato: " + e.getMessage());
        }
        return risultati;
    }

    // --------------------------------------------------------------------------------
    // 11. GET FOLLOWERS: Restituisce lista utenti che seguono un annuncio
    // --------------------------------------------------------------------------------
    @Override
    public List<String> getFollowers(int adId) throws DAOException {
        List<String> followers = new ArrayList<>();
        String sql = "SELECT Utente_Username FROM segue WHERE Annuncio_Codice = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, adId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        followers.add(rs.getString("Utente_Username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero followers: " + e.getMessage());
        }
        return followers;
    }

    // --- Metodo Helper privato per mappare il ResultSet al Bean ---
    private AnnuncioBean mapRowToBean(ResultSet rs) throws SQLException {
        return new AnnuncioBean(
                rs.getInt("codice"),
                rs.getString("titolo"),
                rs.getDouble("importo"),
                rs.getString("descrizione"),
                rs.getString("venditore"),
                rs.getString("categoria")
        );
    }
}