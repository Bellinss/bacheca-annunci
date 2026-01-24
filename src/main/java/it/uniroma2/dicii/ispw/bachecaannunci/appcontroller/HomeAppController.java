package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.List;

public class HomeAppController {

    public List<String> getCategories() throws DAOException {
        return CategoryDAO.getInstance().findAllNames();
    }

    public List<AnnuncioBean> getAllAds() throws DAOException {
        return AdDAO.getInstance().findAll();
    }

    public boolean isUserLogged() {
        return Session.getInstance().getLoggedUser() != null;
    }

    public void logout() {
        Session.getInstance().setLoggedUser(null);
    }

    /**
     * Gestisce la logica complessa di filtraggio combinando Categoria, Ricerca e Preferiti.
     */
    public List<AnnuncioBean> filterAds(String category, String text, boolean onlyFollowed) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();

        // Verifica se è stata selezionata una categoria valida (diversa da null, vuota o "Tutte")
        boolean hasCategory = (category != null && !category.equals("Tutte le categorie") && !category.isEmpty());

        // CASO 1: L'utente vuole vedere solo i seguiti
        if (onlyFollowed) {
            if (user == null) {
                throw new DAOException("Devi effettuare il login per vedere i messaggi o i preferiti.");
            }

            if (hasCategory) {
                // Filtro combinato: Seguiti AND Categoria
                return AdDAO.getInstance().findFollowedByCategory(user.getUsername(), category);
            } else {
                // Solo Seguiti
                return AdDAO.getInstance().findFollowedAds(user.getUsername());
            }
        }

        // CASO 2: Filtri standard (tutti gli annunci, seguiti o meno)
        if (hasCategory) {
            return AdDAO.getInstance().findByCategory(category);
        } else if (text != null && !text.isEmpty()) {
            return AdDAO.getInstance().findByString(text);
        } else {
            // Nessun filtro
            return AdDAO.getInstance().findAll();
        }
    }
}