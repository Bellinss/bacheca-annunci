package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.SellAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SellController {

    @FXML private TextField titoloField;
    @FXML private TextField prezzoField;
    @FXML private TextArea descrizioneArea;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button backButton;

    // Riferimento al Controller Applicativo
    private final SellAppController appController = new SellAppController();

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goHome());
        }
        loadCategories();
    }

    private void loadCategories() {
        try {
            // Delega il recupero dati all'AppController
            List<String> categorie = appController.getCategories();
            categoryComboBox.getItems().addAll(categorie);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento categorie: " + e.getMessage());
        }
    }

    @FXML
    private void handlePublish() {
        // 1. Recupero dati dalla GUI
        String title = titoloField.getText();
        String priceStr = prezzoField.getText();
        String desc = descrizioneArea.getText();
        String category = categoryComboBox.getValue();

        // 2. Validazione Sintattica (UI)
        if (title.isBlank() || priceStr.isBlank() || desc.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Compila tutti i campi!");
            return;
        }

        if (category == null) {
            showAlert(Alert.AlertType.WARNING, "Devi selezionare una categoria!");
            return;
        }

        try {
            // 3. Parsing del prezzo (Validazione formato)
            double price = Double.parseDouble(priceStr);

            // 4. Creazione del Bean (Impacchettamento dati)
            // L'utente è null qui, ci penserà l'AppController a metterlo dalla sessione
            AnnuncioBean bean = new AnnuncioBean(0, title, price, desc, null, category);

            // 5. Delega all'AppController
            appController.publishAd(bean);

            // 6. Successo
            new Alert(Alert.AlertType.INFORMATION, "Annuncio pubblicato con successo!").showAndWait();
            goHome();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Il prezzo deve essere un numero valido (es. 10.50).");
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore pubblicazione: " + e.getMessage());
        }
    }

    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (backButton != null ? backButton : titoloField).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Impossibile tornare alla Home.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}