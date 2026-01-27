package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOMySQL implements CommentDAO {
    private static CommentDAOMySQL instance = null;
    private CommentDAOMySQL() {}

    public static CommentDAOMySQL getInstance() {
        if (instance == null) instance = new CommentDAOMySQL();
        return instance;
    }

    @Override
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
                                    rs.getInt("Codice"),
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

    @Override
    public void addComment(String text, int adId) throws DAOException {
        String sql = "{call invia_commento(?, ?, ?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, text);
                cs.setInt(2, adId);
                cs.registerOutParameter(3, Types.INTEGER);

                cs.execute();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore invio commento: " + e.getMessage());
        }
    }
}