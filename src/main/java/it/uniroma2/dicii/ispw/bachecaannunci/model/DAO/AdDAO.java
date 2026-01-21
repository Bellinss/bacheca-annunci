package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdDAO {
    private static AdDAO instance = null;

    private AdDAO() {}

    public static AdDAO getInstance() {
        if (instance == null) {
            instance = new AdDAO();
        }
        return instance;
    }

    public void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException {
        String sql = "{call crea_annuncio(?,?,?,?,?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, titolo);
                cs.setDouble(2, prezzo);
                cs.setString(3, descrizione);
                cs.setString(4, utente);
                cs.setString(5, categoria);

                // Parametro di output
                cs.registerOutParameter(6, Types.INTEGER);

                cs.execute();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore creazione annuncio: " + e.getMessage());
        }
    }

    public List<AnnuncioBean> findAll() throws DAOException {
        List<AnnuncioBean> annunci = new ArrayList<>();

        String query = "{call lista_annunci()}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(query)) {

                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            AnnuncioBean bean = new AnnuncioBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Titolo"),
                                    rs.getDouble("Importo"),
                                    rs.getString("Descrizione"),
                                    rs.getString("Utente"),
                                    rs.getString("Categoria")
                            );
                            annunci.add(bean);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura annunci: " + e.getMessage());
        }
        return annunci;
    }

    // 2. SEGUI ANNUNCIO
    public boolean seguiAnnuncio(int idAnnuncio) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            Credentials loggedUser = Session.getInstance().getLoggedUser();
            if (loggedUser == null) throw new DAOException("Utente non loggato.");

            String username = loggedUser.getUsername();

            try (CallableStatement cs = conn.prepareCall("{call segui_annuncio(?,?)}")) {
                cs.setString(1, username);
                cs.setInt(2, idAnnuncio);

                cs.execute();
                return true; // Se arriva qui, l'inserimento è riuscito
            }

        } catch (SQLException e) {
            // Gestione DUPLICATE ENTRY (Codice 1062 per MySQL o SQLState che inizia con 23)
            if (e.getErrorCode() == 1062 || e.getSQLState().startsWith("23")) {
                return false; // Ritorna false se lo seguiva già
            }
            throw new DAOException("Errore nel seguire annuncio: " + e.getMessage());
        }
    }

    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            if (conn == null) throw new DAOException("Connessione assente");

            // Recupera l'username dalla sessione
            String username = Session.getInstance().getLoggedUser().getUsername();

            // Chiamata: {call crea_annuncio(titolo, importo, descrizione, utente, categoria, codice_out)}
            try (CallableStatement cs = conn.prepareCall("{call crea_annuncio(?,?,?,?,?,?)}")) {

                cs.setString(1, titolo);      // var_titolo (max 20 chars!)
                cs.setFloat(2, (float) importo); // var_importo
                cs.setString(3, descrizione); // var_descrizione (max 100 chars!)
                cs.setString(4, username);    // var_utente
                cs.setString(5, categoria);   // var_categoria

                // Parametro di output
                cs.registerOutParameter(6, Types.INTEGER);

                cs.execute();

                // Se var_codice > 0, l'inserimento è andato a buon fine
                int nuovoId = cs.getInt(6);
                return nuovoId > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore pubblicazione annuncio: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new DAOException("Nessun utente loggato nella sessione!");
        }
    }

    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();

        String sql = "{call cerca_annunci(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {

                // Passa il testo della ricerca
                cs.setString(1, queryText);

                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            AnnuncioBean bean = new AnnuncioBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Titolo"),
                                    rs.getDouble("Importo"),
                                    rs.getString("Descrizione"),
                                    rs.getString("Utente"),
                                    rs.getString("Categoria")
                            );
                            risultati.add(bean);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nella ricerca: " + e.getMessage());
        }

        return risultati;
    }

    public boolean isFollowing(String username, int idAnnuncio) throws DAOException {
        String query = "SELECT * FROM segue WHERE Utente_Username = ? AND Annuncio_Codice = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setInt(2, idAnnuncio);

                try (ResultSet rs = stmt.executeQuery()) {
                    // Se il ResultSet ha almeno una riga (rs.next() == true),
                    // significa che la relazione esiste.
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore verifica follow: " + e.getMessage());
        }
    }

    public void markAsSold(int idAnnuncio, String venditore) throws DAOException {
        String sql = "{call oggetto_venduto(?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, idAnnuncio);
                cs.setString(2, venditore);

                cs.execute();
            }
        } catch (SQLException e) {
            if ("45011".equals(e.getSQLState())) {
                throw new DAOException("Non sei il proprietario di questo annuncio.");
            }
            throw new DAOException("Errore nel segnare come venduto: " + e.getMessage());
        }
    }

    public List<AnnuncioBean> findByCategory(String categoria) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        String sql = "{call filtra_annunci_categoria(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, categoria);

                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            AnnuncioBean bean = new AnnuncioBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Titolo"),
                                    rs.getDouble("Importo"),
                                    rs.getString("Descrizione"),
                                    rs.getString("Utente"),
                                    rs.getString("Categoria")
                            );
                            risultati.add(bean);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore filtro categoria: " + e.getMessage());
        }
        return risultati;
    }

    public List<AnnuncioBean> findFollowedAds(String username) throws DAOException {
        List<AnnuncioBean> annunci = new ArrayList<>();

        // Chiamiamo la tua procedura esistente
        String sql = "{call lista_annunci_seguiti(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {

                // Passiamo lo username
                cs.setString(1, username);

                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            // Creiamo il bean mappando i campi della tua query
                            AnnuncioBean bean = new AnnuncioBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Titolo"),
                                    rs.getDouble("Importo"),
                                    rs.getString("Descrizione"),
                                    rs.getString("Utente"),
                                    rs.getString("Categoria")
                            );
                            annunci.add(bean);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero annunci seguiti: " + e.getMessage());
        }
        return annunci;
    }

    public List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        String sql = "{call filtra_seguiti_e_categoria(?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, username);
                cs.setString(2, categoria);

                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            AnnuncioBean bean = new AnnuncioBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Titolo"),
                                    rs.getDouble("Importo"),
                                    rs.getString("Descrizione"),
                                    rs.getString("Utente"),
                                    rs.getString("Categoria")
                            );
                            risultati.add(bean);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore filtro combinato: " + e.getMessage());
        }
        return risultati;
    }
}