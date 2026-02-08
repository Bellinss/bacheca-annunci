package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAOFileSystem implements NoteDAO {

    private static final String FILE_NAME = Config.FILE_PATH + "notes.ser";

    public NoteDAOFileSystem() {
        new File(Config.FILE_PATH).mkdirs();
    }

    // --- Helper Load/Save ---
    @SuppressWarnings("unchecked")
    private List<NoteBean> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<NoteBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void save(List<NoteBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio note: " + e.getMessage());
        }
    }

    // --- Implementazione ---

    @Override
    public void createNote(String seller, String text, int adId) throws DAOException {
        List<NoteBean> notes = load();

        // Genera ID incrementale
        int newId = notes.stream()
                .mapToInt(NoteBean::getId)
                .max()
                .orElse(0) + 1;

        // Crea il bean
        NoteBean note = new NoteBean(newId, text, adId);

        notes.add(note);
        save(notes);
    }

    @Override
    public List<NoteBean> retrieveNotes(int adId) throws DAOException {
        List<NoteBean> allNotes = load();

        // Restituisce solo le note associate a quell'annuncio
        return allNotes.stream()
                .filter(n -> n.getIdAnnuncio() == adId)
                .toList();
    }
}