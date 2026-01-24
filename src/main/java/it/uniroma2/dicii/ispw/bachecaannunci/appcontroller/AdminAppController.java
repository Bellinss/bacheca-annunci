package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ReportDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;

public class AdminAppController {

    public void addCategory(String path, String nome) throws DAOException {
        // Logica applicativa: delega al DAO l'inserimento
        CategoryDAO.getInstance().addCategory(path, nome);
    }

    public ReportBean generateUserReport(String username) throws DAOException {
        // Logica applicativa: recupera il report
        return ReportDAO.getInstance().generateReport(username);
    }

    public void logout() {
        // Logica applicativa: pulizia sessione
        Session.getInstance().setLoggedUser(null);
    }
}