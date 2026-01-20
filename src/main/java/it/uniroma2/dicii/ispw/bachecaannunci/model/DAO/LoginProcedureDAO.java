package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginProcedureDAO {

    private static LoginProcedureDAO instance = null;

    private LoginProcedureDAO() {}

    public static LoginProcedureDAO getInstance() {
        if (instance == null) {
            instance = new LoginProcedureDAO();
        }
        return instance;
    }

    public Credentials execute(String username, String password) throws DAOException {
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

                // Usa il metodo statico fromInt dell'Enum Role
                Role role = Role.fromInt(roleId);

                // Se role è null, significa che l'ID restituito dal DB non è 1 o 2 (login fallito)
                if (role == null) {
                    return null;
                }

                // 5. Crea l'oggetto usando il costruttore con 3 parametri (poiché i campi sono final)
                return new Credentials(username, password, role);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore Login nel DAO: " + e.getMessage());
        }
    }
}