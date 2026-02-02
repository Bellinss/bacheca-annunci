package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOMySQL implements MessageDAO {
    private static MessageDAOMySQL instance = null;

    private MessageDAOMySQL() {}

    public static MessageDAOMySQL getInstance() {
        if (instance == null) instance = new MessageDAOMySQL();
        return instance;
    }

    @Override
    public void inviaMessaggio(String sender, String recipient, String text) throws DAOException {
        try {
            Connection conn = ConnectionFactory.getConnection();
            String sql = "{call invia_messaggio(?,?,?,?,?,?,?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, sender);
                cs.setString(2, recipient);
                cs.setString(3, text);
                cs.registerOutParameter(4, Types.INTEGER);
                cs.registerOutParameter(5, Types.INTEGER);
                cs.registerOutParameter(6, Types.DATE);
                cs.registerOutParameter(7, Types.TIME);
                cs.execute();
            }
        } catch (SQLException e) {
            String state = e.getSQLState();
            if ("45003".equals(state)) throw new DAOException("Non puoi inviare un messaggio a te stesso.");
            else if ("45004".equals(state)) throw new DAOException("L'utente selezionato non è un venditore valido.");
            else if ("45007".equals(state)) throw new DAOException("Utente venditore non trovato.");
            else if ("45008".equals(state)) throw new DAOException("Il testo del messaggio non può essere vuoto.");
            throw new DAOException("Errore invio messaggio: " + e.getMessage());
        }
    }

    @Override
    public List<MessageBean> retrieveMessages(String me, String other) throws DAOException {
        List<MessageBean> messaggi = new ArrayList<>();
        try {
            Connection conn = ConnectionFactory.getConnection();
            String sql = "{call lista_messaggi(?,?)}";
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, me);
                cs.setString(2, other);
                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            String sender = rs.getString("Utente_Acquirente");
                            String text = rs.getString("Testo");
                            Date data = rs.getDate("Data");
                            Time time = rs.getTime("Ora");

                            MessageBean msg = new MessageBean(text, data, time, sender);
                            messaggi.add(msg);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if ("45010".equals(e.getSQLState())) return messaggi;
            throw new DAOException("Errore recupero messaggi: " + e.getMessage());
        }
        return messaggi;
    }

    @Override
    public List<String> getActiveConversations(String myUsername) throws DAOException {
        List<String> interlocutori = new ArrayList<>();
        String sql = "SELECT Utente_Acquirente, Utente_Venditore FROM conversazione " +
                "WHERE Utente_Acquirente = ? OR Utente_Venditore = ?";
        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, myUsername);
                ps.setString(2, myUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String acquirente = rs.getString("Utente_Acquirente");
                        String venditore = rs.getString("Utente_Venditore");
                        if (myUsername.equals(acquirente)) {
                            interlocutori.add(venditore);
                        } else {
                            interlocutori.add(acquirente);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero lista conversazioni: " + e.getMessage());
        }
        return interlocutori;
    }
}