package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOMySQL implements CategoryDAO {

    private static CategoryDAOMySQL instance = null;

    private CategoryDAOMySQL() {}

    public static CategoryDAOMySQL getInstance() {
        if (instance == null) instance = new CategoryDAOMySQL();
        return instance;
    }

    @Override
    public List<String> findAllNames() throws DAOException {
        List<String> categorie = new ArrayList<>();
        String sql = "{call visualizza_categoria()}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            categorie.add(rs.getString("Nome"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero categorie: " + e.getMessage());
        }
        return categorie;
    }

    @Override
    public void addCategory(String path, String nomeCategoria) throws DAOException {
        String sql = "{call aggiungi_categoria(?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, path);
                cs.setString(2, nomeCategoria);
                cs.execute();
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new DAOException("Questa categoria esiste già.");
            }
            throw new DAOException("Errore inserimento categoria: " + e.getMessage());
        }
    }
}