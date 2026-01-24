package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

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

    // Segna l'annuncio come venduto nel DB
    public void markAsSold(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Utente non loggato.");

        AdDAO.getInstance().markAsSold(adId, user.getUsername());
    }

    // Aggiunge l'annuncio ai preferiti
    public boolean followAd(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Devi effettuare il login per seguire un annuncio.");

        return AdDAO.getInstance().seguiAnnuncio(user.getUsername(), adId);
    }

    // Controlla se l'annuncio è già seguito
    public boolean isFollowing(int adId) throws DAOException {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) return false;

        return AdDAO.getInstance().isFollowing(user.getUsername(), adId);
    }
}