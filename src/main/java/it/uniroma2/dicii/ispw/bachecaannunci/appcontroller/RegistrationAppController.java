package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.ValidationException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

public class RegistrationAppController {

    public boolean registerUser(UserBean userBean) throws DAOException, ValidationException {

        // --- 1. VALIDAZIONE DATI (Business Logic) ---

        // Controllo Username
        if (userBean.getUsername() == null || userBean.getUsername().length() < 4) {
            throw new ValidationException("Lo username deve avere almeno 4 caratteri.");
        }

        // Controllo Password
        if (userBean.getPassword() == null || userBean.getPassword().length() < 4) {
            throw new ValidationException("La password deve avere almeno 4 caratteri.");
        }

        // Controllo Campi Obbligatori
        if (userBean.getNome() == null || userBean.getNome().trim().isEmpty()) {
            throw new ValidationException("Il nome è obbligatorio.");
        }
        if (userBean.getCognome() == null || userBean.getCognome().trim().isEmpty()) {
            throw new ValidationException("Il cognome è obbligatorio.");
        }

        // --- 2. LOGICA DAO ---
        return DAOFactory.getUserDAO().register(userBean);
    }
}