package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private static CommentDAO instance = null;
    private CommentDAO() {}

    public static CommentDAO getInstance() {
        if (instance == null) instance = new CommentDAO();
        return instance;
    }

    public List<CommentBean> retrieveComments(int adId) throws DAOException {
        List<CommentBean> comments = new ArrayList<>();
        String sql = "{call lista_commenti(?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, adId);
                boolean hasResults = cs.execute();

                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            comments.add(new CommentBean(
                                    rs.getInt("Codice"), // Assumo che la colonna chiave sia Codice
                                    rs.getString("Testo"),
                                    rs.getInt("Annuncio_Codice")
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero commenti: " + e.getMessage());
        }
        return comments;
    }

    public void addComment(String text, int adId) throws DAOException {
        String sql = "{call invia_commento(?, ?, ?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, text);
                cs.setInt(2, adId);
                // Il terzo parametro è OUT (l'ID del nuovo commento)
                cs.registerOutParameter(3, Types.INTEGER);

                cs.execute();
                // Se ti servisse l'ID generato: int newId = cs.getInt(3);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore invio commento: " + e.getMessage());
        }
    }
}