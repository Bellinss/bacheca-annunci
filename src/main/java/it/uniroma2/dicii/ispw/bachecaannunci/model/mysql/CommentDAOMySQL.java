package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOMySQL implements CommentDAO {

    private static CommentDAOMySQL instance = null;

    private CommentDAOMySQL() {}

    public static CommentDAOMySQL getInstance() {
        if (instance == null) {
            instance = new CommentDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. RETRIEVE COMMENTS: Recupera i commenti per un annuncio
    // --------------------------------------------------------------------------------
    @Override
    public List<CommentBean> retrieveComments(int adId) throws DAOException {
        List<CommentBean> comments = new ArrayList<>();
        String sql = "SELECT * FROM commenti WHERE id_annuncio = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, adId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // Mappatura ResultSet -> Bean
                        // Assumendo le colonne: id, testo, id_annuncio
                        comments.add(new CommentBean(
                                rs.getInt("id"),
                                rs.getString("testo"),
                                rs.getInt("id_annuncio")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero commenti: " + e.getMessage());
        }
        return comments;
    }

    // --------------------------------------------------------------------------------
    // 2. ADD COMMENT: Inserisce un nuovo commento
    // --------------------------------------------------------------------------------
    @Override
    public void addComment(String text, int adId) throws DAOException {
        String sql = "INSERT INTO commenti (testo, id_annuncio) VALUES (?, ?)";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, text);
                ps.setInt(2, adId);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore invio commento: " + e.getMessage());
        }
    }
}