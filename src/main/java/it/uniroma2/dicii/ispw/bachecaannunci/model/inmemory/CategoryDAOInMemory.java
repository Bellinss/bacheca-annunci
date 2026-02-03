package it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAOInMemory implements CategoryDAO {

    // "Database" volatile delle categorie
    private static final List<String> categories = new ArrayList<>();

    // Inizializzazione dati di default per la Demo
    static {
        categories.add("Elettronica");
        categories.add("Libri");
        categories.add("Arredamento");
        categories.add("Abbigliamento");
        categories.add("Sport");
        categories.add("Veicoli");
    }

    @Override
    public List<String> findAllNames() throws DAOException {
        // Restituisce una copia per proteggere la lista interna
        return new ArrayList<>(categories);
    }

    @Override
    public void addCategory(String nomeCategoria) throws DAOException {
        // Nota: Il parametro 'path' è ignorato in questa implementazione InMemory,
        // dato che non gestiamo file o immagini associate alle categorie in questa demo semplice.

        // 1. Controllo duplicati (Case Insensitive)
        for (String cat : categories) {
            if (cat.equalsIgnoreCase(nomeCategoria)) {
                throw new DAOException("La categoria '" + nomeCategoria + "' esiste già.");
            }
        }

        // 2. Aggiunta alla lista
        categories.add(nomeCategoria);
    }
}