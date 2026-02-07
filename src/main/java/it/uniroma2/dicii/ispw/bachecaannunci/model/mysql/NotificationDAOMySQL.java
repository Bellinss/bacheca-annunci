package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOMySQL implements NotificationDAO {

    private static NotificationDAOMySQL instance = null;

    private NotificationDAOMySQL() {}

    public static NotificationDAOMySQL getInstance() {
        if (instance == null) {
            instance = new NotificationDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. RETRIEVE NOTIFICATIONS: Select diretta ordinata per data
    // --------------------------------------------------------------------------------
    @Override
    public List<NotificationBean> retrieveNotifications(String username) throws DAOException {
        List<NotificationBean> list = new ArrayList<>();
        String sql = "SELECT * FROM notifiche WHERE username = ? ORDER BY data DESC";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new NotificationBean(
                                rs.getInt("codice"),
                                rs.getString("username"),
                                rs.getTimestamp("data"),
                                rs.getString("testo")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero notifiche: " + e.getMessage());
        }
        return list;
    }

    // --------------------------------------------------------------------------------
    // 2. CLEAR NOTIFICATIONS: Delete diretta per l'utente
    // --------------------------------------------------------------------------------
    @Override
    public void clearNotifications(String username) throws DAOException {
        String sql = "DELETE FROM notifiche WHERE username = ?";
        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore cancellazione notifiche: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 3. ADD NOTIFICATION: Insert diretta
    // --------------------------------------------------------------------------------
    @Override
    public void addNotification(String username, String testo) throws DAOException {
        // Usa NOW() di MySQL o passa un Timestamp da Java
        String sql = "INSERT INTO notifiche (username, testo, data) VALUES (?, ?, NOW())";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, testo);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore invio notifica: " + e.getMessage());
        }
    }
}