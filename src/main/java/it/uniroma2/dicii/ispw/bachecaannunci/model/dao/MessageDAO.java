package it.uniroma2.dicii.ispw.bachecaannunci.model.dao;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;
import java.util.List;

public interface MessageDAO {
    void inviaMessaggio(String sender, String recipient, String text) throws DAOException;
    List<MessageBean> retrieveMessages(String me, String other) throws DAOException;
    List<String> getActiveConversations(String myUsername) throws DAOException;
}