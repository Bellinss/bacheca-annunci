package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import java.util.List;

public interface CategoryDAO {
    List<String> findAllNames() throws DAOException;
    void addCategory(String nomeCategoria) throws DAOException;
}