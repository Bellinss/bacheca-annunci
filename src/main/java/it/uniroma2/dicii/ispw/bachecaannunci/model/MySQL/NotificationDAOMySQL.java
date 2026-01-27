package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOMySQL implements NotificationDAO {
    private static NotificationDAOMySQL instance = null;

    private NotificationDAOMySQL() {}

    public static NotificationDAOMySQL getInstance() {
        if (instance == null) instance = new NotificationDAOMySQL();
        return instance;
    }

    @Override
    public List<NotificationBean> retrieveNotifications(String username) throws DAOException {
        List<NotificationBean> list = new ArrayList<>();
        String sql = "{call lista_notifiche(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, username);
                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            list.add(new NotificationBean(
                                    rs.getInt("Codice"),
                                    rs.getString("Username_Utente"),
                                    rs.getTimestamp("Data"),
                                    rs.getString("Testo")
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero notifiche: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void clearNotifications(String username) throws DAOException {
        String sql = "{call elimina_notifiche(?)}";
        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, username);
                cs.execute();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore cancellazione notifiche: " + e.getMessage());
        }
    }

    @Override
    public void addNotification(String username, String testo) throws DAOException {
        // Query diretta per inserire la notifica (senza usare procedure complesse)
        String sql = "INSERT INTO notifica (Username_Utente, Testo, Data) VALUES (?, ?, NOW())";

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