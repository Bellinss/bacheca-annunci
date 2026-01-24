package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ConnectionFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.LoginProcedureDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.sql.SQLException;

public class LoginAppController {

    public Credentials login(String username, String password) throws DAOException, SQLException {
        // 1. Chiama il DAO per verificare le credenziali
        Credentials cred = LoginProcedureDAO.getInstance().execute(username, password);

        // 2. Se le credenziali sono valide, imposta il Ruolo nel DB e salva la Sessione
        if (cred != null) {
            // Cambio ruolo nel DB per i permessi
            ConnectionFactory.changeRole(cred.getRole());

            // Salvataggio utente in sessione
            Session.getInstance().setLoggedUser(cred);
        }

        return cred;
    }
}