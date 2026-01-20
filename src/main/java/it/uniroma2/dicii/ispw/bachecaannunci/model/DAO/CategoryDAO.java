package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static CategoryDAO instance = null;
    private CategoryDAO() {}
    public static CategoryDAO getInstance() {
        if (instance == null) instance = new CategoryDAO();
        return instance;
    }

    public List<String> findAllNames() throws DAOException {
        List<String> categorie = new ArrayList<>();
        // Usa la tua stored procedure esistente
        String sql = "{call visualizza_categoria()}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                boolean hasResults = cs.execute();
                if (hasResults) {
                    try (ResultSet rs = cs.getResultSet()) {
                        while (rs.next()) {
                            // Assumo la colonna si chiami "Nome" come nel tuo DB
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

    public void addCategory(String path, String nomeCategoria) throws DAOException {
        String sql = "{call aggiungi_categoria(?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setString(1, path); // Path
                cs.setString(2, nomeCategoria); // Nome
                cs.execute();
            }
        } catch (SQLException e) {
            // Gestione errore chiave duplicata (se la categoria esiste già)
            if (e.getSQLState().startsWith("23")) {
                throw new DAOException("Questa categoria esiste già.");
            }
            throw new DAOException("Errore inserimento categoria: " + e.getMessage());
        }
    }
}