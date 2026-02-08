package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.UserDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UserDAOInMemory implements UserDAO {

    // "Database" volatile degli utenti
    private static final List<UserBean> users = new ArrayList<>();

    // Blocco statico per inizializzare utenti di prova (Utile per Demo immediata)
    static {
        // Admin predefinito
        UserBean admin = new UserBean(
                "admin", "password", "Super", "Admin",
                Date.valueOf("1990-01-01"), "Email", "admin@bacheca.it"
        );
        admin.setResidenza("Roma");
        admin.setFatturazione("Roma");
        users.add(admin);

        // Utente predefinito
        UserBean user = new UserBean(
                "mario", "rossi", "Mario", "Rossi",
                Date.valueOf("1995-05-20"), "Telefono", "3331234567"
        );
        user.setResidenza("Milano");
        user.setFatturazione("Milano");
        users.add(user);
    }

    @Override
    public Credentials login(String username, String password) throws DAOException {
        // Cerca l'utente nella lista
        for (UserBean u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {

                // Determina il ruolo
                Role role = Role.UTENTE;
                if (u.getUsername().equalsIgnoreCase("admin")) {
                    role = Role.AMMINISTRATORE;
                }

                // Restituisce le credenziali valide
                return new Credentials(u.getUsername(), u.getPassword(), role);
            }
        }

        return null;
    }

    @Override
    public boolean register(UserBean newUser) throws DAOException {
        // 1. Controllo unicità username
        for (UserBean u : users) {
            if (u.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                throw new DAOException("Username '" + newUser.getUsername() + "' già esistente (Demo Mode).");
            }
        }

        // 2. Aggiunta alla lista in memoria
        users.add(newUser);
        return true;
    }
}