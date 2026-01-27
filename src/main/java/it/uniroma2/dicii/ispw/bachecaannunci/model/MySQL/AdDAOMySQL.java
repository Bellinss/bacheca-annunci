package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO; // Importa l'interfaccia
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// 1. RINOMINA LA CLASSE E IMPLEMENTA L'INTERFACCIA
public class AdDAOMySQL implements AdDAO {

    private static AdDAOMySQL instance = null;

    private AdDAOMySQL() {}

    public static AdDAOMySQL getInstance() {
        if (instance == null) {
            instance = new AdDAOMySQL();
        }
        return instance;
    }

    @Override
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

    @Override
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

    @Override
    public boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            Credentials loggedUser = Session.getInstance().getLoggedUser();
            if (loggedUser == null) throw new DAOException("Utente non loggato.");

            try (CallableStatement cs = conn.prepareCall("{call segui_annuncio(?,?)}")) {
                cs.setString(1, username);
                cs.setInt(2, idAnnuncio);

                cs.execute();
                return true;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || e.getSQLState().startsWith("23")) {
                return false;
            }
            throw new DAOException("Errore nel seguire annuncio: " + e.getMessage());
        }
    }

    @Override
    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            if (conn == null) throw new DAOException("Connessione assente");

            String username = Session.getInstance().getLoggedUser().getUsername();

            try (CallableStatement cs = conn.prepareCall("{call crea_annuncio(?,?,?,?,?,?)}")) {

                cs.setString(1, titolo);
                cs.setFloat(2, (float) importo);
                cs.setString(3, descrizione);
                cs.setString(4, username);
                cs.setString(5, categoria);

                cs.registerOutParameter(6, Types.INTEGER);

                cs.execute();

                int nuovoId = cs.getInt(6);
                return nuovoId > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore pubblicazione annuncio: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new DAOException("Nessun utente loggato nella sessione!");
        }
    }

    @Override
    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        List<AnnuncioBean> risultati = new ArrayList<>();
        String sql = "{call cerca_annunci(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
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

    @Override
    public boolean isFollowing(String username, int idAnnuncio) throws DAOException {
        String query = "SELECT * FROM segue WHERE Utente_Username = ? AND Annuncio_Codice = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setInt(2, idAnnuncio);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore verifica follow: " + e.getMessage());
        }
    }

    @Override
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

    @Override
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

    @Override
    public List<AnnuncioBean> findFollowedAds(String username) throws DAOException {
        List<AnnuncioBean> annunci = new ArrayList<>();
        String sql = "{call lista_annunci_seguiti(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, username);
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
            throw new DAOException("Errore recupero annunci seguiti: " + e.getMessage());
        }
        return annunci;
    }

    @Override
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
}