package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.util.List;

public class NoteAppController {

    public void addNote(String text, int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Devi essere loggato.");
        if (text == null || text.trim().isEmpty()) throw new DAOException("La nota non può essere vuota.");
        DAOFactory.getNoteDAO().createNote(user.getUsername(), text, adId);

        // -------------------------------------------------------------
        // 2. LOGICA NOTIFICHE AI FOLLOWER
        // -------------------------------------------------------------

        // A. Recupera chi segue questo annuncio
        List<String> followers = DAOFactory.getAdDAO().getFollowers(adId);

        // B. Prepara il testo della notifica
        String notificaTxt = "Il venditore ha aggiunto una nota all'annuncio #" + adId + ": " + text;

        // C. Invia la notifica a tutti i follower
        for (String followerUsername : followers) {
            // Non inviare la notifica a se stessi (se il venditore si auto-segue per test)
            if (!followerUsername.equals(user.getUsername())) {
                DAOFactory.getNotificationDAO().addNotification(followerUsername, notificaTxt);
            }
        }
    }

    public List<NoteBean> getNotes(int adId) throws DAOException {
        return DAOFactory.getNoteDAO().retrieveNotes(adId);
    }
}