package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;
import java.util.List;

public interface NotificationDAO {
    List<NotificationBean> retrieveNotifications(String username) throws DAOException;
    void clearNotifications(String username) throws DAOException;
    void addNotification(String username, String testo) throws DAOException;
}