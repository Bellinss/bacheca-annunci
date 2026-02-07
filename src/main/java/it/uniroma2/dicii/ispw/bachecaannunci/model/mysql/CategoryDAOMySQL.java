package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.CategoryDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOMySQL implements CategoryDAO {

    private static CategoryDAOMySQL instance = null;

    private CategoryDAOMySQL() {}

    public static CategoryDAOMySQL getInstance() {
        if (instance == null) {
            instance = new CategoryDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------------------------------
    // 1. FIND ALL NAMES: Recupera tutti i nomi delle categorie
    // --------------------------------------------------------------------------------
    @Override
    public List<String> findAllNames() throws DAOException {
        List<String> categorie = new ArrayList<>();
        String sql = "SELECT nome FROM categorie";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        categorie.add(rs.getString("nome"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero categorie: " + e.getMessage());
        }
        return categorie;
    }

    // --------------------------------------------------------------------------------
    // 2. ADD CATEGORY: Inserisce una nuova categoria
    // --------------------------------------------------------------------------------
    @Override
    public void addCategory(String nomeCategoria) throws DAOException {
        String sql = "INSERT INTO categorie (nome) VALUES (?)";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nomeCategoria);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            // Gestione errore duplicati (MySQL error 1062)
            if (e.getErrorCode() == 1062 || e.getSQLState().startsWith("23")) {
                throw new DAOException("Questa categoria esiste già.");
            }
            throw new DAOException("Errore inserimento categoria: " + e.getMessage());
        }
    }
}