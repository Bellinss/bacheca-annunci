package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.UserDAO; // L'interfaccia creata prima
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOFileSystem implements UserDAO {

    // File dove vengono salvati gli utenti registrati
    private static final String FILE_NAME = Config.DEMO_FILE_PATH + "users.ser";

    public UserDAOFileSystem() {
        // Assicura che la cartella demo_data esista
        new File(Config.DEMO_FILE_PATH).mkdirs();
    }

    // --------------------------------------------------------
    // HELPER: Caricamento e Salvataggio (Serializzazione)
    // --------------------------------------------------------

    @SuppressWarnings("unchecked")
    private List<UserBean> loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<UserBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<UserBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio utenti su file: " + e.getMessage());
        }
    }

    // --------------------------------------------------------
    // LOGICA DI LOGIN (Sostituisce LoginProcedureDAO)
    // --------------------------------------------------------

    @Override
    public Credentials login(String username, String password) throws DAOException {
        // 1. Carica tutti gli utenti dal file
        List<UserBean> users = loadUsers();

        // 2. Cerca una corrispondenza
        for (UserBean u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {

                // Trovato! Restituisci le credenziali.
                // Nota: Nella demo, assegniamo di default il ruolo UTENTE (o quello salvato se lo gestisci nel bean)
                // Se il tuo UserBean non ha il campo ruolo, usiamo Role.UTENTE fisso per la demo.
                return new Credentials(u.getUsername(), u.getPassword(), Role.UTENTE);
            }
        }

        // 3. Nessuna corrispondenza trovata
        return null;
    }

    // --------------------------------------------------------
    // LOGICA DI REGISTRAZIONE (Sostituisce RegistrationProcedureDAO)
    // --------------------------------------------------------

    @Override
    public boolean register(UserBean newUser) throws DAOException {
        List<UserBean> users = loadUsers();

        // 1. Controllo unicità username
        for (UserBean u : users) {
            if (u.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                throw new DAOException("Username già esistente (Demo Mode).");
            }
        }

        // 2. Aggiungi e salva
        users.add(newUser);
        saveUsers(users);

        return true;
    }
}