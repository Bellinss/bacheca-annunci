package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;
import java.util.List;

public interface CommentDAO {
    List<CommentBean> retrieveComments(int adId) throws DAOException;
    void addComment(String text, int adId) throws DAOException;
}