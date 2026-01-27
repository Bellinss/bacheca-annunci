package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

public interface UserDAO {
    Credentials login(String username, String password) throws DAOException;
    boolean register(UserBean user) throws DAOException;
}