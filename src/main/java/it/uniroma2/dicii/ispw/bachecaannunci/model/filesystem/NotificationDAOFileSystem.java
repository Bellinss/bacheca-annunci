package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDAOFileSystem implements NotificationDAO {

    private static final String FILE_NAME = Config.DEMO_FILE_PATH + "notifications.ser";

    public NotificationDAOFileSystem() {
        new File(Config.DEMO_FILE_PATH).mkdirs();
    }

    // --- Helper Load/Save ---
    @SuppressWarnings("unchecked")
    private List<NotificationBean> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<NotificationBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void save(List<NotificationBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio notifiche: " + e.getMessage());
        }
    }

    // --- Implementazione ---

    @Override
    public List<NotificationBean> retrieveNotifications(String username) throws DAOException {
        List<NotificationBean> all = load();

        // Filtra solo le notifiche dell'utente specifico
        return all.stream()
                .filter(n -> n.getUsername().equals(username)) // Assicurati che NotificationBean abbia getUsername()
                .collect(Collectors.toList());
    }

    @Override
    public void clearNotifications(String username) throws DAOException {
        List<NotificationBean> all = load();

        // Rimuove tutte le notifiche di quell'utente
        all.removeIf(n -> n.getUsername().equals(username));

        save(all);
    }

    @Override
    public void addNotification(String username, String testo) throws DAOException {
        List<NotificationBean> all = load();

        // Calcola nuovo ID
        int newId = all.stream().mapToInt(NotificationBean::getCodice).max().orElse(0) + 1;

        // Crea notifica con timestamp corrente
        Timestamp now = new Timestamp(System.currentTimeMillis());
        NotificationBean bean = new NotificationBean(newId, username, now, testo);

        all.add(bean);
        save(all);
    }
}