package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.Collections;
import java.util.List;

public class InboxAppController {

    public List<String> getActiveConversations() throws DAOException {
        // Recupera l'utente dalla sessione
        Credentials user = Session.getInstance().getLoggedUser();

        // Se non c'è utente loggato, ritorna lista vuota (o lancia eccezione a seconda delle preferenze)
        if (user == null) {
            return Collections.emptyList();
        }

        // Chiama il DAO per ottenere la lista degli interlocutori
        return MessageDAO.getInstance().getActiveConversations(user.getUsername());
    }
}