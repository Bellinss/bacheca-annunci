package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import java.sql.SQLException;

public interface GenericProcedureDAO<P> {
    P execute(Object... params) throws DAOException, SQLException;
}
