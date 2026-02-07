package it.uniroma2.dicii.ispw.bachecaannunci.model.dao;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import java.util.List;

public interface AdDAO {
    void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException;
    List<AnnuncioBean> findAll() throws DAOException;
    boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException;
    boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException;
    List<AnnuncioBean> findByString(String queryText) throws DAOException;
    boolean isFollowing(String username, int idAnnuncio) throws DAOException;
    void markAsSold(int idAnnuncio, String venditore) throws DAOException;
    List<AnnuncioBean> findByCategory(String categoria) throws DAOException;
    List<AnnuncioBean> findFollowedAds(String username) throws DAOException;
    List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException;
    List<String> getFollowers(int idAd) throws DAOException;
}