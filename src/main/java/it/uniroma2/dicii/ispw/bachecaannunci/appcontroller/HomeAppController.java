package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.util.ArrayList;
import java.util.List;

public class HomeAppController {

    public List<String> getCategories() throws DAOException {
        // 1. USA FACTORY PER CATEGORIE
        return DAOFactory.getCategoryDAO().findAllNames();
    }

    public List<AnnuncioBean> getAllAds() throws DAOException {
        // 2. USA FACTORY: Tutti gli annunci
        return DAOFactory.getAdDAO().findAll();
    }

    public boolean isUserLogged() {
        return Session.getInstance().getLoggedUser() != null;
    }

    public void logout() {
        Session.getInstance().setLoggedUser(null);
    }

    public List<AnnuncioBean> filterAds(String category, String text, boolean onlyFollowed) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();

        boolean hasCategory = (category != null && !category.equals("Tutte le categorie") && !category.isEmpty());

        // CASO 1: L'utente vuole vedere solo i seguiti
        if (onlyFollowed) {
            if (user == null) {
                throw new DAOException("Devi effettuare il login per vedere i messaggi o i preferiti.");
            }

            if (hasCategory) {
                // 3. USA FACTORY: Filtro combinato (Seguiti + Categoria)
                return DAOFactory.getAdDAO().findFollowedByCategory(user.getUsername(), category);
            } else {
                // 4. USA FACTORY: Solo Seguiti
                return DAOFactory.getAdDAO().findFollowedAds(user.getUsername());
            }
        }

        // CASO 2: Filtri standard
        if (hasCategory) {
            // 5. USA FACTORY: Filtro Categoria
            return DAOFactory.getAdDAO().findByCategory(category);

        } else if (text != null && !text.isEmpty()) {
            // 6. USA FACTORY: Filtro Testo
            return DAOFactory.getAdDAO().findByString(text);

        } else {
            // 7. USA FACTORY: Nessun filtro (Tutti)
            return DAOFactory.getAdDAO().findAll();
        }
    }

    public List<NotificationBean> getNotifications() throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) return new ArrayList<>();

        return DAOFactory.getNotificationDAO().retrieveNotifications(user.getUsername());
    }

    public void clearAllNotifications() throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user != null) {
            // 8. USA FACTORY PER NOTIFICHE
            DAOFactory.getNotificationDAO().clearNotifications(user.getUsername());
        }
    }
}