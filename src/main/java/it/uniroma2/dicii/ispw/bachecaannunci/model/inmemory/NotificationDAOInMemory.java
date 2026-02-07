package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDAOInMemory implements NotificationDAO {

    // "Database" volatile delle notifiche
    private static final List<NotificationBean> notifications = new ArrayList<>();
    private static int idCounter = 1;

    // Dati di default per la Demo
    static {
        // Notifica di benvenuto per l'utente "mario"
        notifications.add(new NotificationBean(
                idCounter++,
                "mario",
                Timestamp.from(Instant.now()),
                "Benvenuto nella versione Demo di Bacheca Annunci!"
        ));
    }

    @Override
    public List<NotificationBean> retrieveNotifications(String username) throws DAOException {
        // Filtra le notifiche per l'utente specifico
        return notifications.stream()
                .filter(n -> n.getUsername().equals(username))
                .collect(Collectors.toList());
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