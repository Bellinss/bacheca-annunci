package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.AdminAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminHomeController {

    @FXML private TextField categoryNameField;
    @FXML private TextField targetUserField;
    @FXML private TextArea reportOutputArea;

    // Riferimento al Controller Applicativo
    private final AdminAppController appController = new AdminAppController();

    @FXML
    private void handleAddCategory() {
        // 1. Recupero dati dalla GUI
        String nome = categoryNameField.getText().trim();

        // 2. Validazione sintattica (GUI)
        if (nome.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Inserisci sia Path che Nome per la categoria.");
            return;
        }

        try {
            // 3. Delega all'Applicativo
            appController.addCategory(nome);

            // 4. Aggiornamento GUI (Successo)
            showAlert(Alert.AlertType.INFORMATION, "Categoria '" + nome + "' aggiunta con successo!");
            categoryNameField.clear();

        } catch (DAOException e) {
            // 5. Gestione Errori
            showAlert(Alert.AlertType.ERROR, "Errore: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReport() {
        String username = targetUserField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Inserisci uno username.");
            return;
        }

        try {
            // Chiamata all'Applicativo
            ReportBean report = appController.generateUserReport(username);

            // Aggiornamento GUI
            if (report != null) {
                reportOutputArea.setText(report.toString());
            } else {
                reportOutputArea.setText("Nessun dato trovato per l'utente " + username);
            }

        } catch (DAOException e) {
            reportOutputArea.setText("Errore generazione report: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        // Chiamata all'Applicativo per pulire la sessione
        appController.logout();

        // Navigazione (compito del controller grafico)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) categoryNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore durante il logout: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}