package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AdDAOFileSystem implements AdDAO {

    private static final String ADS_FILE = Config.FILE_PATH + "annunci.ser";
    private static final String FOLLOWS_FILE = Config.FILE_PATH + "segue.ser";

    private static final String COUNTERS_FILE = Config.FILE_PATH + "counters.properties";

    public AdDAOFileSystem() {
        new File(Config.FILE_PATH).mkdirs();
    }

    @SuppressWarnings("unchecked")
    private List<AnnuncioBean> loadAds() {
        File file = new File(ADS_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<AnnuncioBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveAds(List<AnnuncioBean> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADS_FILE))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio annunci: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Set<Integer>> loadFollows() {
        File file = new File(FOLLOWS_FILE);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Set<Integer>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveFollows(Map<String, Set<Integer>> map) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FOLLOWS_FILE))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio follow: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Generatore di ID persistente
    // -----------------------------------------------------------------
    private synchronized int generateNewId() throws DAOException {
        File file = new File(COUNTERS_FILE);
        Properties props = new Properties();
        int nextId = 1; // Valore di partenza default

        // 1. Carica il contatore attuale (se esiste il file)
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                String lastIdStr = props.getProperty("last_ad_id");

                if (lastIdStr != null) {
                    nextId = parseNextId(lastIdStr);
                }

            } catch (IOException e) {
                throw new DAOException("Errore lettura contatori: " + e.getMessage());
            }
        }

        // 2. Salva il nuovo contatore
        props.setProperty("last_ad_id", String.valueOf(nextId));
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Contatori ID Bacheca Annunci");
        } catch (IOException e) {
            throw new DAOException("Errore aggiornamento contatori: " + e.getMessage());
        }

        return nextId;
    }

    private int parseNextId(String lastIdStr) {
        try {
            return Integer.parseInt(lastIdStr) + 1;
        } catch (NumberFormatException e) {
            return 1; // Fallback se il file è corrotto
        }
    }

    // -----------------------------------------------------------------
    // IMPLEMENTAZIONE INTERFACCIA AdDAO
    // -----------------------------------------------------------------

    @Override
    public void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException {
        List<AnnuncioBean> ads = loadAds();

        int newId = generateNewId();

        AnnuncioBean newAd = new AnnuncioBean(newId, titolo, prezzo, descrizione, utente, categoria);
        ads.add(newAd);

        saveAds(ads);
    }

    @Override
    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        String username;
        try {
            username = Session.getInstance().getLoggedUser().getUsername();
        } catch (NullPointerException e) {
            throw new DAOException("Nessun utente loggato nella sessione!");
        }
        createAd(titolo, importo, descrizione, username, categoria);
        return true;
    }

    @Override
    public List<AnnuncioBean> findAll() throws DAOException {
        return loadAds();
    }

    @Override
    public void markAsSold(int idAnnuncio, String venditore) throws DAOException {
        List<AnnuncioBean> ads = loadAds();
        Optional<AnnuncioBean> target = ads.stream().filter(a -> a.getId() == idAnnuncio).findFirst();

        if (target.isEmpty()) throw new DAOException("Annuncio non trovato.");

        AnnuncioBean ad = target.get();
        if (!ad.getVenditore().equals(venditore)) {
            throw new DAOException("Non sei il proprietario di questo annuncio.");
        }

        ads.remove(ad);
        saveAds(ads);
    }

    @Override
    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        List<AnnuncioBean> allAds = loadAds();
        String lowerQuery = queryText.toLowerCase();
        return allAds.stream()
                .filter(a -> a.getTitolo().toLowerCase().contains(lowerQuery) ||
                        a.getDescrizione().toLowerCase().contains(lowerQuery))
                .toList();
    }

    @Override
    public List<AnnuncioBean> findByCategory(String categoria) throws DAOException {
        return loadAds().stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException {
        Map<String, Set<Integer>> follows = loadFollows();
        Set<Integer> userFollows = follows.computeIfAbsent(username, k -> new HashSet<>());
        if (userFollows.contains(idAnnuncio)) return false;
        userFollows.add(idAnnuncio);
        saveFollows(follows);
        return true;
    }

    @Override
    public boolean isFollowing(String username, int idAnnuncio) throws DAOException {
        Map<String, Set<Integer>> follows = loadFollows();
        Set<Integer> userFollows = follows.get(username);
        return userFollows != null && userFollows.contains(idAnnuncio);
    }

    @Override
    public List<AnnuncioBean> findFollowedAds(String username) throws DAOException {
        Map<String, Set<Integer>> follows = loadFollows();
        Set<Integer> followedIds = follows.getOrDefault(username, new HashSet<>());
        if (followedIds.isEmpty()) return new ArrayList<>();
        List<AnnuncioBean> allAds = loadAds();
        return allAds.stream().filter(a -> followedIds.contains(a.getId())).toList();
    }

    @Override
    public List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException {
        return findFollowedAds(username).stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public List<String> getFollowers(int adId) throws DAOException {
        List<String> followers = new ArrayList<>();
        Map<String, Set<Integer>> allFollows = loadFollows();
        for (Map.Entry<String, Set<Integer>> entry : allFollows.entrySet()) {
            if (entry.getValue().contains(adId)) {
                followers.add(entry.getKey());
            }
        }
        return followers;
    }
}