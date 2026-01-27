package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.List;

public class SellAppController {

    // Recupera la lista delle categorie dal DB
    public List<String> getCategories() throws DAOException {
        return DAOFactory.getCategoryDAO().findAllNames();
    }

    // Pubblica l'annuncio associandolo all'utente in sessione
    public void publishAd(AnnuncioBean bean) throws DAOException {
        // 1. Recupera l'utente dalla sessione (Logica di business)
        Credentials user = Session.getInstance().getLoggedUser();

        if (user == null) {
            throw new DAOException("Devi effettuare il login per pubblicare un annuncio.");
        }

        // 2. Chiama il DAO passando i dati
        DAOFactory.getAdDAO().insert(
                bean.getTitolo(),
                bean.getImporto(),
                bean.getDescrizione(),
                bean.getCategoria()
        );
    }
}