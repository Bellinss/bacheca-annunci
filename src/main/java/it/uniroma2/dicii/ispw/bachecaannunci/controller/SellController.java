package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox; // Importante
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

    @FXML
    public void initialize() {
        loadCategories();

        if (backButton != null) {
            backButton.setOnAction(e -> goHome());
        }
    }

    private void loadCategories() {
        try {
            // Usa il DAO creato nel passaggio precedente
            List<String> categorie = CategoryDAO.getInstance().findAllNames();
            categoryComboBox.getItems().addAll(categorie);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento categorie: " + e.getMessage());
        }
    }

    @FXML
    private void handlePublish() {
        // 1. Recupera i valori
        String title = titoloField.getText().trim();
        String priceStr = prezzoField.getText().trim();
        String desc = descrizioneArea.getText().trim();
        String category = categoryComboBox.getValue(); // Valore selezionato

        // 2. Validazioni
        if (title.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Compila tutti i campi di testo.");
            return;
        }

        if (category == null) {
            showAlert(Alert.AlertType.WARNING, "Devi selezionare una categoria!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            Credentials user = Session.getInstance().getLoggedUser();

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Utente non loggato.");
                return;
            }

            // 3. Salva nel Database
            AdDAO.getInstance().createAd(title, price, desc, user.getUsername(), category);

            // 4. Successo
            new Alert(Alert.AlertType.INFORMATION, "Annuncio pubblicato con successo!").showAndWait();
            goHome();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Il prezzo deve essere un numero valido (es. 10.50).");
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Database: " + e.getMessage());
        }
    }

    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}