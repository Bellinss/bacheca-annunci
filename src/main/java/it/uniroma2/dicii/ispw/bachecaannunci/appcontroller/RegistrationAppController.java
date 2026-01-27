package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

public class RegistrationAppController {

    public boolean registerUser(UserBean userBean) throws DAOException {
        return DAOFactory.getUserDAO().register(userBean);
    }
}