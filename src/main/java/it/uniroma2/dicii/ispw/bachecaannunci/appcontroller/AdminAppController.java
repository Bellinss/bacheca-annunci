package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;

public class AdminAppController {

    public void addCategory(String path, String nome) throws DAOException {
        DAOFactory.getCategoryDAO().addCategory(path, nome);
    }

    public ReportBean generateUserReport(String username) throws DAOException {
        return DAOFactory.getReportDAO().generateReport(username);
    }

    public void logout() {
        // Logica di sessione per il logout
        Session.getInstance().setLoggedUser(null);
    }
}