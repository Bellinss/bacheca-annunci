package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageDAOInMemory implements MessageDAO {

    private static final List<MessageBean> messages = new ArrayList<>();

    @Override
    public void inviaMessaggio(String sender, String recipient, String text) throws DAOException {
        MessageBean msg = new MessageBean(
                text,
                Date.valueOf(LocalDate.now()),
                Time.valueOf(LocalTime.now()),
                sender,
                recipient
        );
        messages.add(msg);
    }

    @Override
    public List<MessageBean> retrieveMessages(String me, String other) throws DAOException {
        return messages.stream()
                .filter(m -> (m.getMittente().equals(me) && m.getDestinatario().equals(other)) ||
                        (m.getMittente().equals(other) && m.getDestinatario().equals(me)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getActiveConversations(String myUsername) throws DAOException {
        Set<String> contacts = new HashSet<>();
        for (MessageBean m : messages) {
            if (m.getMittente().equals(myUsername)) {
                contacts.add(m.getDestinatario());
            } else if (m.getDestinatario().equals(myUsername)) {
                contacts.add(m.getMittente());
            }
        }
        return new ArrayList<>(contacts);
    }
}