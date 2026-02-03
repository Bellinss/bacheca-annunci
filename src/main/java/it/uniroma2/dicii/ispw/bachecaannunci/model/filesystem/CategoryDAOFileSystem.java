package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOFileSystem implements CategoryDAO {

    private static final String FILE_NAME = Config.FILE_PATH + "categories.ser";

    public CategoryDAOFileSystem() {
        File folder = new File(Config.FILE_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Inizializza con categorie di default se il file non esiste
        // (Così la demo è subito utilizzabile)
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            List<String> defaults = new ArrayList<>();
            defaults.add("Elettronica");
            defaults.add("Libri");
            defaults.add("Arredamento");
            defaults.add("Abbigliamento");
            defaults.add("Sport");
            try {
                save(defaults);
            } catch (DAOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void save(List<String> list) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio categorie: " + e.getMessage());
        }
    }

    @Override
    public List<String> findAllNames() throws DAOException {
        return load();
    }

    @Override
    public void addCategory(String nomeCategoria) throws DAOException {
        List<String> categories = load();

        // Controllo duplicati (Case Insensitive)
        for (String cat : categories) {
            if (cat.equalsIgnoreCase(nomeCategoria)) {
                throw new DAOException("Questa categoria esiste già.");
            }
        }

        categories.add(nomeCategoria);
        save(categories);
    }
}