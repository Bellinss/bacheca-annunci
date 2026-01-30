package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdDAOInMemory implements AdDAO {

    private static final List<AnnuncioBean> ads = new ArrayList<>();
    private static final Map<String, List<Integer>> followingMap = new HashMap<>();
    private static int idCounter = 1;

    @Override
    public void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException {
        AnnuncioBean newAd = new AnnuncioBean(idCounter++, titolo, prezzo, descrizione, utente, categoria);
        ads.add(newAd);
    }

    @Override
    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        createAd(titolo, importo, descrizione, "Sconosciuto", categoria);
        return true;
    }

    @Override
    public List<AnnuncioBean> findAll() throws DAOException {
        return new ArrayList<>(ads);
    }

    @Override
    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        if (queryText == null || queryText.isBlank()) return new ArrayList<>();
        String lowerQuery = queryText.toLowerCase();
        return ads.stream()
                .filter(a -> a.getTitolo().toLowerCase().contains(lowerQuery) ||
                        a.getDescrizione().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnuncioBean> findByCategory(String categoria) throws DAOException {
        return ads.stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsSold(int idAnnuncio, String venditore) throws DAOException {
        ads.removeIf(a -> a.getId() == idAnnuncio && a.getVenditore().equals(venditore));
        for (List<Integer> followedIds : followingMap.values()) {
            followedIds.remove(Integer.valueOf(idAnnuncio));
        }
    }

    @Override
    public boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException {
        boolean exists = ads.stream().anyMatch(a -> a.getId() == idAnnuncio);
        if (!exists) return false;

        followingMap.computeIfAbsent(username, k -> new ArrayList<>());
        List<Integer> userFavorites = followingMap.get(username);

        if (!userFavorites.contains(idAnnuncio)) {
            userFavorites.add(idAnnuncio);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFollowing(String username, int idAnnuncio) throws DAOException {
        if (!followingMap.containsKey(username)) return false;
        return followingMap.get(username).contains(idAnnuncio);
    }

    @Override
    public List<AnnuncioBean> findFollowedAds(String username) throws DAOException {
        if (!followingMap.containsKey(username)) return new ArrayList<>();
        List<Integer> ids = followingMap.get(username);
        return ads.stream().filter(a -> ids.contains(a.getId())).collect(Collectors.toList());
    }

    @Override
    public List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException {
        return findFollowedAds(username).stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFollowers(int idAd) throws DAOException {
        List<String> followers = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : followingMap.entrySet()) {
            if (entry.getValue().contains(idAd)) {
                followers.add(entry.getKey());
            }
        }
        return followers;
    }
}