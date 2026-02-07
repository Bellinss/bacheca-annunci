package it.uniroma2.dicii.ispw.bachecaannunci.model.dao;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;
import java.util.List;

public interface NoteDAO {
    void createNote(String seller, String text, int adId) throws DAOException;
    List<NoteBean> retrieveNotes(int adId) throws DAOException;
}