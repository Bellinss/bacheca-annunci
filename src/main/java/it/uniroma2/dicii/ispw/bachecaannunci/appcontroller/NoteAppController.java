package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.util.List;

public class NoteAppController {

    public void addNote(String text, int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Devi essere loggato.");
        if (text == null || text.trim().isEmpty()) throw new DAOException("La nota non può essere vuota.");

        NoteDAO.getInstance().createNote(user.getUsername(), text, adId);
    }

    public List<NoteBean> getNotes(int adId) throws DAOException {
        return NoteDAO.getInstance().retrieveNotes(adId);
    }
}