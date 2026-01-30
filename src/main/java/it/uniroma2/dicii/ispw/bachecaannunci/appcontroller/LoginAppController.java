package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.sql.SQLException;

public class LoginAppController {

    public Credentials login(String username, String password) throws DAOException {
        // 1. Chiama il DAO per verificare le credenziali
        // Il DAOFactory istanzia la versione corretta (Memory, FS o MySQL) in base alla Config
        Credentials cred = DAOFactory.getUserDAO().login(username, password);

        // 2. Se le credenziali sono valide, salva la Sessione applicativa
        if (cred != null) {
            Session.getInstance().setLoggedUser(cred);
        }

        return cred;
    }
}