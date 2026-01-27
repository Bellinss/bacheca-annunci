package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;

import java.util.List;

public class CommentAppController {

    public List<CommentBean> getComments(int adId) throws DAOException {
        return CommentDAO.getInstance().retrieveComments(adId);
    }

    public void postComment(String text, int adId) throws DAOException {
        if (text == null || text.trim().isEmpty()) {
            throw new DAOException("Il commento non può essere vuoto.");
        }
        CommentDAO.getInstance().addComment(text, adId);
    }
}