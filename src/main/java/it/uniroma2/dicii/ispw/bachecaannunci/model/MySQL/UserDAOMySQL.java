package it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.UserDAO;         // L'interfaccia
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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
        String sql = "{call login(?,?,?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {

                // 1. Input
                cs.setString(1, username);
                cs.setString(2, password);

                // 2. Output (il ruolo)
                cs.registerOutParameter(3, Types.INTEGER);

                // 3. Esegui
                cs.execute();

                // 4. Leggi il risultato
                int roleId = cs.getInt(3);

                // Converte l'intero nell'Enum Role
                Role role = Role.fromInt(roleId);

                // Se role è null, il login è fallito (l'ID non era valido)
                if (role == null) {
                    return null;
                }

                try {
                    ConnectionFactory.changeRole(role);
                } catch (SQLException e) {
                    throw new DAOException("Errore cambio ruolo DB: " + e.getMessage());
                }

                // 5. Ritorna le credenziali
                return new Credentials(username, password, role);
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
        String sql = "{call registra_utente(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            Connection conn = ConnectionFactory.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {

                // 1. Username
                cs.setString(1, userBean.getUsername());
                // 2. Password
                cs.setString(2, userBean.getPassword());
                // 3. Nome
                cs.setString(3, userBean.getNome());
                // 4. Cognome
                cs.setString(4, userBean.getCognome());
                // 5. Data di Nascita (Passata direttamente come java.sql.Date)
                cs.setDate(5, userBean.getDataNascita());
                // 6. Residenza
                cs.setString(6, userBean.getResidenza());

                // 7. Fatturazione (gestisce anche il null se opzionale)
                if (userBean.getFatturazione() != null && !userBean.getFatturazione().isEmpty()) {
                    cs.setString(7, userBean.getFatturazione());
                } else {
                    cs.setNull(7, Types.VARCHAR);
                }

                // 8. Tipo Recapito
                cs.setString(8, userBean.getTipoRecapito());
                // 9. Recapito
                cs.setString(9, userBean.getRecapito());

                cs.execute();
                return true;
            }
        } catch (SQLException e) {
            // Gestione specifica per duplicati (es. username già esistente)
            if (e.getErrorCode() == 1062 || e.getSQLState().startsWith("23")) {
                throw new DAOException("Username già esistente.");
            }
            throw new DAOException("Errore Registrazione: " + e.getMessage());
        }
    }
}