package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOInMemory implements NotificationDAO {

    // "Database" volatile delle notifiche
    private final List<NotificationBean> notifications = new ArrayList<>();
    private int idCounter = 1;

    @Override
    public List<NotificationBean> retrieveNotifications(String username) throws DAOException {
        // Filtra le notifiche per l'utente specifico
        return notifications.stream()
                .filter(n -> n.getUsername().equals(username))
                .toList();
    }

    @Override
    public void addNotification(String username, String testo) throws DAOException {
        // Crea una nuova notifica con Timestamp corrente
        NotificationBean notification = new NotificationBean(
                idCounter++,
                username,
                Timestamp.from(Instant.now()),
                testo
        );
        notifications.add(notification);
    }

    @Override
    public void clearNotifications(String username) throws DAOException {
        // Rimuove tutte le notifiche associate a quell'username
        notifications.removeIf(n -> n.getUsername().equals(username));
    }
}