package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.RegistrationProcedureDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

public class RegistrationAppController {

    public boolean registerUser(UserBean userBean) throws DAOException {
        // Logica Applicativa: chiama il DAO per salvare l'utente
        return RegistrationProcedureDAO.getInstance().execute(userBean);
    }
}