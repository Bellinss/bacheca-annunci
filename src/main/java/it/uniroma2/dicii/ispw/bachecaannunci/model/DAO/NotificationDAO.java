package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private static NotificationDAO instance = null;
    private NotificationDAO() {}

    public static NotificationDAO getInstance() {
        if (instance == null) instance = new NotificationDAO();
        return instance;
    }

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
}