package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOMySQL implements MessageDAO {

    private static MessageDAOMySQL instance = null;

    private MessageDAOMySQL() {}

    public static MessageDAOMySQL getInstance() {
        if (instance == null) {
            instance = new MessageDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. INVIA MESSAGGIO: Insert diretta con Data e Ora calcolate in Java
    // --------------------------------------------------------------------------------
    @Override
    public void inviaMessaggio(String sender, String recipient, String text) throws DAOException {
        String sql = "INSERT INTO messaggi (mittente, destinatario, testo, data, ora) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                // Calcolo Data e Ora correnti in Java (Logica Applicativa)
                long now = System.currentTimeMillis();
                java.sql.Date sqlDate = new java.sql.Date(now);
                java.sql.Time sqlTime = new java.sql.Time(now);

                ps.setString(1, sender);
                ps.setString(2, recipient);
                ps.setString(3, text);
                ps.setDate(4, sqlDate);
                ps.setTime(5, sqlTime);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore invio messaggio: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------
    // 2. RECUPERA MESSAGGI: Select bidirezionale (mittente <-> destinatario)
    // --------------------------------------------------------------------------------
    @Override
    public List<MessageBean> retrieveMessages(String sender, String recipient) throws DAOException {
        List<MessageBean> messaggi = new ArrayList<>();

        // Seleziona i messaggi scambiati tra i due utenti in entrambe le direzioni
        // Ordinati per data e ora per ricostruire la chat
        String sql = "SELECT * FROM messaggi " +
                "WHERE (mittente = ? AND destinatario = ?) " +
                "OR (mittente = ? AND destinatario = ?) " +
                "ORDER BY data ASC, ora ASC";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                // Parametri per (sender -> recipient)
                ps.setString(1, sender);
                ps.setString(2, recipient);

                // Parametri per (recipient -> sender)
                ps.setString(3, recipient);
                ps.setString(4, sender);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        messaggi.add(new MessageBean(
                                rs.getString("testo"),
                                rs.getDate("data"),
                                rs.getTime("ora"),
                                rs.getString("mittente"),
                                rs.getString("destinatario")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero messaggi: " + e.getMessage());
        }
        return messaggi;
    }

    // --------------------------------------------------------------------------------
    // 3. CONVERSAZIONI ATTIVE: Query DISTINCT sulla tabella messaggi
    // --------------------------------------------------------------------------------
    @Override
    public List<String> getActiveConversations(String myUsername) throws DAOException {
        List<String> interlocutori = new ArrayList<>();

        String sql = "SELECT DISTINCT destinatario as utente FROM messaggi WHERE mittente = ? " +
                "UNION " +
                "SELECT DISTINCT mittente as utente FROM messaggi WHERE destinatario = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, myUsername);
                ps.setString(2, myUsername);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String utente = rs.getString("utente");
                        // Escludo me stesso per sicurezza (anche se la query non dovrebbe estrarlo)
                        if (utente != null && !utente.equals(myUsername)) {
                            interlocutori.add(utente);
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