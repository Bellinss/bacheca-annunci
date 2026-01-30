package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ReportDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;

import java.sql.Date;
import java.time.LocalDate;

public class ReportDAOInMemory implements ReportDAO {

    @Override
    public ReportBean generateReport(String targetUsername) throws DAOException {
        Date now = Date.valueOf(LocalDate.now());

        return new ReportBean(targetUsername, now, 0f, 0, 0);
    }
}