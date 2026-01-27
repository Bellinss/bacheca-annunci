package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO; // Assicurati che questa sia l'interfaccia!
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AdDAOFileSystem implements AdDAO {

    // File per salvare gli annunci
    private static final String ADS_FILE = Config.DEMO_FILE_PATH + "annunci.ser";
    // File per salvare le relazioni "segue" (Map<String, Set<Integer>>)
    private static final String FOLLOWS_FILE = Config.DEMO_FILE_PATH + "segue.ser";

    public AdDAOFileSystem() {
        // Assicura che la cartella esista
        new File(Config.DEMO_FILE_PATH).mkdirs();
    }

    // -----------------------------------------------------------------
    // HELPER METHODS: Caricamento e Salvataggio su File
    // -----------------------------------------------------------------

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
            throw new DAOException("Errore salvataggio annunci su file: " + e.getMessage());
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
            throw new DAOException("Errore salvataggio follow su file: " + e.getMessage());
        }
    }

    @Override
    public List<String> getFollowers(int adId) throws DAOException {
        // Logica Demo: Carica la lista dei preferiti (es. da follows.ser)
        // E filtra quelli che hanno adId uguale a quello passato.
        // Esempio fittizio:
        List<String> followers = new ArrayList<>();

        // ... codice per caricare i preferiti ...
        // for (FollowBean f : allFollows) {
        //      if (f.getAdId() == adId) followers.add(f.getUsername());
        // }

        return followers;
    }

    // -----------------------------------------------------------------
    // IMPLEMENTAZIONE INTERFACCIA AdDAO
    // -----------------------------------------------------------------

    @Override
    public void createAd(String titolo, double prezzo, String descrizione, String utente, String categoria) throws DAOException {
        List<AnnuncioBean> ads = loadAds();

        // Generazione ID incrementale
        int newId = ads.stream().mapToInt(AnnuncioBean::getId).max().orElse(0) + 1;

        AnnuncioBean newAd = new AnnuncioBean(newId, titolo, prezzo, descrizione, utente, categoria);
        ads.add(newAd);

        saveAds(ads);
    }

    @Override
    public boolean insert(String titolo, double importo, String descrizione, String categoria) throws DAOException {
        // Recupera utente dalla sessione (replica comportamento DB)
        String username;
        try {
            username = Session.getInstance().getLoggedUser().getUsername();
        } catch (NullPointerException e) {
            throw new DAOException("Nessun utente loggato nella sessione!");
        }

        createAd(titolo, importo, descrizione, username, categoria);
        return true; // Se createAd non lancia eccezione, è successo
    }

    @Override
    public List<AnnuncioBean> findAll() throws DAOException {
        return loadAds();
    }

    @Override
    public List<AnnuncioBean> findByString(String queryText) throws DAOException {
        List<AnnuncioBean> allAds = loadAds();
        String lowerQuery = queryText.toLowerCase();

        return allAds.stream()
                .filter(a -> a.getTitolo().toLowerCase().contains(lowerQuery) ||
                        a.getDescrizione().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnuncioBean> findByCategory(String categoria) throws DAOException {
        List<AnnuncioBean> allAds = loadAds();
        return allAds.stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsSold(int idAnnuncio, String venditore) throws DAOException {
        List<AnnuncioBean> ads = loadAds();

        Optional<AnnuncioBean> target = ads.stream()
                .filter(a -> a.getId() == idAnnuncio)
                .findFirst();

        if (target.isEmpty()) {
            throw new DAOException("Annuncio non trovato.");
        }

        AnnuncioBean ad = target.get();
        // Controllo permessi (come nel DB)
        if (!ad.getVenditore().equals(venditore)) {
            throw new DAOException("Non sei il proprietario di questo annuncio.");
        }

        // Nella demo version, "Venduto" significa rimuoverlo dalla lista
        ads.remove(ad);
        saveAds(ads);
    }

    // -----------------------------------------------------------------
    // GESTIONE FOLLOW (SEGUI ANNUNCIO)
    // -----------------------------------------------------------------

    @Override
    public boolean seguiAnnuncio(String username, int idAnnuncio) throws DAOException {
        Map<String, Set<Integer>> follows = loadFollows();

        // Ottieni (o crea) il set di annunci seguiti per questo utente
        Set<Integer> userFollows = follows.computeIfAbsent(username, k -> new HashSet<>());

        // Se lo segue già, ritorna false (comportamento DB duplicate entry)
        if (userFollows.contains(idAnnuncio)) {
            return false;
        }

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
        // 1. Carica gli ID seguiti
        Map<String, Set<Integer>> follows = loadFollows();
        Set<Integer> followedIds = follows.getOrDefault(username, new HashSet<>());

        if (followedIds.isEmpty()) return new ArrayList<>();

        // 2. Carica gli annunci e filtra
        List<AnnuncioBean> allAds = loadAds();
        return allAds.stream()
                .filter(a -> followedIds.contains(a.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnuncioBean> findFollowedByCategory(String username, String categoria) throws DAOException {
        // Combina i due filtri: deve essere seguito AND della categoria giusta
        List<AnnuncioBean> followedAds = findFollowedAds(username);

        return followedAds.stream()
                .filter(a -> a.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }
}