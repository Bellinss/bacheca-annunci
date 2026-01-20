package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private TilePane adsContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Carica le categorie nel menu a tendina
        loadCategories();

        // 2. Carica tutti gli annunci
        loadAds();
    }

    private void loadCategories() {
        try {
            List<String> categorie = CategoryDAO.getInstance().findAllNames();

            // Aggiungo un'opzione per resettare il filtro
            categoryComboBox.getItems().add("Tutte le categorie");
            categoryComboBox.getItems().addAll(categorie);

            // Seleziona "Tutte" di default
            categoryComboBox.getSelectionModel().selectFirst();

        } catch (DAOException e) {
            showError("Impossibile caricare le categorie.");
        }
    }

    @FXML
    private void handleApplyFilters() {
        // Recupera la categoria selezionata
        String selectedCat = categoryComboBox.getValue();

        // Se è null o è "Tutte le categorie", ricarica tutto
        if (selectedCat == null || selectedCat.equals("Tutte le categorie")) {
            loadAds();
            return;
        }

        try {
            // Pulisce la griglia
            adsContainer.getChildren().clear();

            // Chiama il DAO per filtrare
            List<AnnuncioBean> filteredAds = AdDAO.getInstance().findByCategory(selectedCat);

            // Popola la griglia
            populateGrid(filteredAds);

            if(filteredAds.isEmpty()) {
                showInfo("Nessun annuncio in questa categoria");
            }

        } catch (DAOException e) {
            showError("Errore filtro: " + e.getMessage());
        }
    }

    // --- CARICAMENTO ANNUNCI ---

    private void loadAds() {
        try {
            // 1. Recupera la lista dal Database.
            List<AnnuncioBean> annunci = AdDAO.getInstance().findAll();

            // 2. Popola la griglia grafica
            populateGrid(annunci);

        } catch (DAOException e) {
            showError("Errore nel caricamento degli annunci: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();

        // Se la barra è vuota, ricarica tutti gli annunci
        if (query.isEmpty()) {
            loadAds();
            return;
        }

        try {
            // Cerca nel DB filtrando per la stringa
            List<AnnuncioBean> risultati = AdDAO.getInstance().findByString(query);

            // Aggiorna la griglia con i soli risultati trovati
            populateGrid(risultati);

            if (risultati.isEmpty()) {
               showInfo("Nessun annuncio trovato per: " + query);
            }

        } catch (DAOException e) {
            showError("Errore nella ricerca: " + e.getMessage());
        }
    }

    private void populateGrid(List<AnnuncioBean> annunci) {
        // Rimuove visivamente anche gli annunci che sono stati appena venduti.
        adsContainer.getChildren().clear();

        try {
            for (AnnuncioBean annuncio : annunci) {
                // Carica il singolo blocchetto (item.fxml)
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/item.fxml"));
                VBox cardBox = fxmlLoader.load();

                // Passa i dati al controller del blocchetto
                ItemController itemController = fxmlLoader.getController();
                itemController.setData(annuncio);

                // Aggiungi alla griglia
                adsContainer.getChildren().add(cardBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Errore grafico nel caricamento delle card.");
        }
    }

    // --- NAVIGAZIONE ---

    @FXML
    private void goToSell() {
        // Controllo Login: Solo chi è registrato può vendere
        if (Session.getInstance().getLoggedUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Devi effettuare il login per pubblicare un annuncio.").showAndWait();
            return;
        }

        changeScene("/sell.fxml");
    }

    @FXML
    private void goToInbox() {
        // Controllo Login: Solo chi è registrato può vedere i messaggi
        if (Session.getInstance().getLoggedUser() == null) {
            new Alert(Alert.AlertType.WARNING, "Devi fare il login per vedere i messaggi.").showAndWait();
            return;
        }

        changeScene("/inbox.fxml");
    }

    @FXML
    private void handleLogout() {
        // 1. Logout Logico
        Session.getInstance().setLoggedUser(null);

        // 2. Logout Grafico (Torna al login)
        changeScene("/login.fxml");
    }

    // Metodo Helper per cambiare scena ed evitare duplicazione di codice try-catch
    private void changeScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) adsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossibile caricare la pagina: " + fxmlFile);
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    // Aggiungi questo metodo per gestire messaggi informativi
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}