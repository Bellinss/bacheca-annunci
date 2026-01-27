package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.List;

public class CommentAppController {

    public List<CommentBean> getComments(int adId) throws DAOException {
        return CommentDAO.getInstance().retrieveComments(adId);
    }

    public void postComment(String text, int adId) throws DAOException {
        // 1. Controlli base
        if (text == null || text.trim().isEmpty()) {
            throw new DAOException("Il commento non può essere vuoto.");
        }

        // 2. Recupero Utente
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) {
            throw new DAOException("Devi effettuare il login per commentare.");
        }

        // 3. Formatto la stringa QUI
        String formattedText = user.getUsername() + ": " + text;

        // 4. Invio al DAO per l'inserimento
        CommentDAO.getInstance().addComment(formattedText, adId);
    }
}