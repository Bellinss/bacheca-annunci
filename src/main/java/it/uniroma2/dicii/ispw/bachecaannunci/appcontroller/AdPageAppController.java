package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

import java.util.List;

public class AdPageAppController {

    // Verifica se c'è un utente loggato
    public boolean isLoggedIn() {
        return Session.getInstance().getLoggedUser() != null;
    }

    // Verifica se l'utente loggato è il venditore dell'annuncio
    public boolean isOwner(String sellerUsername) {
        Credentials currentUser = Session.getInstance().getLoggedUser();
        if (currentUser == null) return false;
        return currentUser.getUsername().equals(sellerUsername);
    }

    // Segna l'annuncio come venduto nel DB (o nel File System)
    public void markAsSold(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Utente non loggato.");
        DAOFactory.getAdDAO().markAsSold(adId, user.getUsername());

        String testoNotifica = "Complimenti! Hai venduto l'oggetto con ID " + adId;
        DAOFactory.getNotificationDAO().addNotification(user.getUsername(), testoNotifica);

        // Notifica i follower che l'annuncio è stato venduto
        List<String> followers = DAOFactory.getAdDAO().getFollowers(adId);
        for (String follower : followers) {
            if (!follower.equals(user.getUsername())) {
                DAOFactory.getNotificationDAO().addNotification(follower, "L'oggetto #" + adId + " che segui è stato venduto!");
            }
        }
    }

    // Aggiunge l'annuncio ai preferiti
    public boolean followAd(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Devi effettuare il login per seguire un annuncio.");
        return DAOFactory.getAdDAO().seguiAnnuncio(user.getUsername(), adId);
    }

    // Controlla se l'annuncio è già seguito
    public boolean isFollowing(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) return false;
        return DAOFactory.getAdDAO().isFollowing(user.getUsername(), adId);
    }
}