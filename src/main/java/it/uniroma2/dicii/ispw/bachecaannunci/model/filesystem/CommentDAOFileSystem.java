package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDAOFileSystem implements CommentDAO {

    private static final String FILE_NAME = Config.FILE_PATH + "comments.ser";

    public CommentDAOFileSystem() {
        new File(Config.FILE_PATH).mkdirs();
    }

    // --- Helper Load/Save ---
    @SuppressWarnings("unchecked")
    private List<CommentBean> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<CommentBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void save(List<CommentBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio commenti: " + e.getMessage());
        }
    }

    // --- Implementazione ---

    @Override
    public List<CommentBean> retrieveComments(int adId) throws DAOException {
        List<CommentBean> allComments = load();

        // Filtra solo i commenti che appartengono all'annuncio specificato
        return allComments.stream()
                .filter(c -> c.getIdAnnuncio() == adId) // Assicurati che CommentBean abbia il getter getIdAnnuncio()
                .toList();
    }

    @Override
    public void addComment(String text, int adId) throws DAOException {
        List<CommentBean> allComments = load();

        // Genera un nuovo ID incrementale
        int newId = allComments.stream()
                .mapToInt(CommentBean::getId) // Assicurati che CommentBean abbia getId()
                .max()
                .orElse(0) + 1;

        // Crea il nuovo bean
        CommentBean newComment = new CommentBean(newId, text, adId);

        allComments.add(newComment);
        save(allComments);
    }
}