package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NoteDAOInMemory implements NoteDAO {
    private final List<NoteBean> notes = new ArrayList<>();
    private int idCounter = 1;

    @Override
    public void createNote(String seller, String text, int adId) throws DAOException {
        notes.add(new NoteBean(idCounter++, text, adId));
    }

    @Override
    public List<NoteBean> retrieveNotes(int adId) throws DAOException {
        return notes.stream()
                .filter(n -> n.getIdAnnuncio() == adId)
                .toList();
    }
}