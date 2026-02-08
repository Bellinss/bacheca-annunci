package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageDAOFileSystem implements MessageDAO {

    private static final String FILE_NAME = Config.FILE_PATH + "messages.ser";

    public MessageDAOFileSystem() {
        new File(Config.FILE_PATH).mkdirs();
    }

    // --- Helper Load/Save ---
    @SuppressWarnings("unchecked")
    private List<MessageBean> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<MessageBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void save(List<MessageBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio messaggi: " + e.getMessage());
        }
    }

    // --- Implementazione ---

    @Override
    public void inviaMessaggio(String sender, String recipient, String text) throws DAOException {
        // 1. Controlli di validazione (simulano i controlli del DB/SP)
        if (sender.equals(recipient)) {
            throw new DAOException("Non puoi inviare un messaggio a te stesso.");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new DAOException("Il testo del messaggio non può essere vuoto.");
        }

        List<MessageBean> messages = load();

        // 2. Creazione dati temporali
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());

        // 3. Creazione Bean
        // IMPORTANTE: Il costruttore deve supportare sender E recipient per funzionare su file
        // Se il tuo costruttore attuale è diverso, adattalo o usa i setter.
        MessageBean msg = new MessageBean(text, date, time, sender);
        msg.setDestinatario(recipient); // Assicurati di avere questo setter o passalo nel costruttore

        messages.add(msg);
        save(messages);
    }

    @Override
    public List<MessageBean> retrieveMessages(String me, String other) throws DAOException {
        List<MessageBean> allMessages = load();

        // Filtra i messaggi scambiati tra ME e OTHER (in entrambe le direzioni)
        return allMessages.stream()
                .filter(m -> (m.getMittente().equals(me) && m.getDestinatario().equals(other)) ||
                        (m.getMittente().equals(other) && m.getDestinatario().equals(me)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getActiveConversations(String myUsername) throws DAOException {
        List<MessageBean> allMessages = load();
        Set<String> interlocutori = new HashSet<>();

        // Cerca tutti gli utenti con cui ho scambiato almeno un messaggio
        for (MessageBean m : allMessages) {
            if (m.getMittente().equals(myUsername)) {
                interlocutori.add(m.getDestinatario());
            } else if (m.getDestinatario().equals(myUsername)) {
                interlocutori.add(m.getMittente());
            }
        }

        return new ArrayList<>(interlocutori);
    }
}