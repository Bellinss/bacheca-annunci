package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.ValidationException;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.List;

public class SellAppController {

    // Recupera la lista delle categorie dal DB
    public List<String> getCategories() throws DAOException {
        return DAOFactory.getCategoryDAO().findAllNames();
    }

    // Pubblica l'annuncio associandolo all'utente in sessione
    public void publishAd(AnnuncioBean bean) throws DAOException, ValidationException {
        // 1. Recupera l'utente dalla sessione (Logica di business)
        Credentials user = Session.getInstance().getLoggedUser();

        if (user == null) {
            throw new DAOException("Devi effettuare il login per pubblicare un annuncio.");
        }

        if (bean.getTitolo() == null || bean.getTitolo().trim().isEmpty()) {
            throw new ValidationException("Il titolo dell'annuncio non può essere vuoto.");
        }

        if (bean.getImporto() < 0) {
            throw new ValidationException("Il prezzo non può essere negativo.");
        }

        if (bean.getDescrizione() == null || bean.getDescrizione().length() < 10) {
            throw new ValidationException("La descrizione deve contenere almeno 10 caratteri.");
        }

        // 2. Chiama il DAO passando i dati
        DAOFactory.getAdDAO().createAd(
                bean.getTitolo(),
                bean.getImporto(),
                bean.getDescrizione(),
                user.getUsername(),
                bean.getCategoria()
        );
    }
}