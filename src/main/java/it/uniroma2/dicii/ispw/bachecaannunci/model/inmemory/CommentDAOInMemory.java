package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDAOInMemory implements CommentDAO {
    private static final List<CommentBean> comments = new ArrayList<>();
    private static int idCounter = 1;

    @Override
    public List<CommentBean> retrieveComments(int adId) throws DAOException {
        return comments.stream()
                .filter(c -> c.getIdAnnuncio() == adId)
                .collect(Collectors.toList());
    }

    @Override
    public void addComment(String text, int adId) throws DAOException {
        comments.add(new CommentBean(idCounter++, text, adId));
    }
}