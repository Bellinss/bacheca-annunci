package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

public class RegistrationProcedureDAO implements GenericProcedureDAO<Boolean> {

    private static RegistrationProcedureDAO instance = null;

    private RegistrationProcedureDAO() {}

    public static RegistrationProcedureDAO getInstance() {
        if (instance == null) {
            instance = new RegistrationProcedureDAO();
        }
        return instance;
    }

    @Override
    public Boolean execute(Object... params) throws DAOException {
        UserBean user = (UserBean) params[0];

        try {
            Connection conn = ConnectionFactory.getConnection();
            if (conn == null) throw new DAOException("Connessione DB assente.");

            // Chiamata alla stored procedure
            String sql = "{call registra_utente(?,?,?,?,?,?,?,?,?)}";

            try (CallableStatement cs = conn.prepareCall(sql)) {
                int i = 1;
                cs.setString(i++, user.getUsername());
                cs.setString(i++, user.getPassword());
                cs.setString(i++, user.getNome());
                cs.setString(i++, user.getCognome());
                cs.setDate(i++, Date.valueOf(user.getDataNascita()));
                cs.setString(i++, user.getResidenza());

                // --- INDIRIZZO DI FATTURAZIONE OPZIONALE ---
                String fatturazione = user.getFatturazione();

                // Se la stringa è null o vuota (""), mandiamo NULL al database
                if (fatturazione == null || fatturazione.trim().isEmpty()) {
                    cs.setNull(i++, Types.VARCHAR);
                } else {
                    cs.setString(i++, fatturazione);
                }
                // -------------------------------------------

                cs.setString(i++, user.getTipoRecapito());
                cs.setString(i++, user.getRecapito());

                cs.execute();
                return true;
            }

        } catch (SQLException e) {
            // Gestione degli errori specifici della tua procedura
            String state = e.getSQLState();

            if ("45015".equals(state)) {
                throw new DAOException("Email non valida (formato errato).");
            } else if ("45016".equals(state)) {
                throw new DAOException("Cellulare non valido (formato errato).");
            } else if (state != null && state.startsWith("23")) {
                throw new DAOException("Username già esistente.");
            }

            throw new DAOException("Errore registrazione: " + e.getMessage());
        }
    }
}