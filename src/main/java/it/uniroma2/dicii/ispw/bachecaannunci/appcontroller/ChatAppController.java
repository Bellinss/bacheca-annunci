package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;

import java.util.List;

public class ChatAppController {

    // Recupera lo username dell'utente loggato dalla sessione
    public String getLoggedUsername() {
        Credentials cred = Session.getInstance().getLoggedUser();
        return (cred != null) ? cred.getUsername() : null;
    }

    // Recupera i messaggi scambiati con un altro utente
    public List<MessageBean> getMessages(String otherUser) throws DAOException {
        String myUser = getLoggedUsername();
        if (myUser == null) throw new DAOException("Utente non loggato.");

        return DAOFactory.getMessageDAO().retrieveMessages(myUser, otherUser);
    }

    // Invia un messaggio
    public void sendMessage(String otherUser, String text) throws DAOException {
        String myUser = getLoggedUsername();
        if (myUser == null) throw new DAOException("Utente non loggato.");

        DAOFactory.getMessageDAO().inviaMessaggio(myUser, otherUser, text);
    }
}