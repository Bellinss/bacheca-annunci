package it.uniroma2.dicii.ispw.bachecaannunci.model.mysql;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.UserDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOMySQL implements UserDAO {

    private static UserDAOMySQL instance = null;

    private UserDAOMySQL() {}

    public static UserDAOMySQL getInstance() {
        if (instance == null) {
            instance = new UserDAOMySQL();
        }
        return instance;
    }

    // --------------------------------------------------------
    // LOGIN
    // --------------------------------------------------------
    @Override
    public Credentials login(String username, String password) throws DAOException {
        String sql = "SELECT ruolo_id FROM utenti WHERE username = ? AND password = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int roleId = rs.getInt("ruolo_id");
                        Role role = Role.fromInt(roleId);

                        if (role == null) {
                            throw new DAOException("Ruolo non valido nel database.");
                        }

                        try {
                            ConnectionFactory.changeRole(role);
                        } catch (SQLException e) {
                            throw new DAOException("Errore cambio ruolo DB: " + e.getMessage());
                        }

                        return new Credentials(username, password, role);
                    } else {
                        // Utente o password errati
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore Login nel DAO: " + e.getMessage());
        }
    }

    // --------------------------------------------------------
    // REGISTRAZIONE
    // --------------------------------------------------------
    @Override
    public boolean register(UserBean userBean) throws DAOException {
        // Il ruolo_id viene impostato a 2 (UTENTE) di default per i nuovi registrati
        String sql = "INSERT INTO utenti (username, password, nome, cognome, data_nascita, residenza, fatturazione, tipo_recapito, recapito, ruolo_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 2)";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, userBean.getUsername());
                ps.setString(2, userBean.getPassword());
                ps.setString(3, userBean.getNome());
                ps.setString(4, userBean.getCognome());
                ps.setDate(5, userBean.getDataNascita());
                ps.setString(6, userBean.getResidenza());

                // Gestione dei campi opzionali (Fatturazione)
                if (userBean.getFatturazione() != null && !userBean.getFatturazione().isEmpty()) {
                    ps.setString(7, userBean.getFatturazione());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }

                ps.setString(8, userBean.getTipoRecapito());
                ps.setString(9, userBean.getRecapito());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // Gestione specifica per duplicati (Codice errore MySQL 1062 = Duplicate Entry)
            if (e.getErrorCode() == 1062) {
                throw new DAOException("Username già esistente.");
            }
            throw new DAOException("Errore Registrazione: " + e.getMessage());
        }
    }
}