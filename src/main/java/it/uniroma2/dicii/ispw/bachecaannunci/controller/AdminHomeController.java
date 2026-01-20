package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ReportDAO;
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

    @FXML private TextField categoryPathField;
    @FXML private TextField categoryNameField;
    @FXML private TextField targetUserField;
    @FXML private TextArea reportOutputArea;

    @FXML
    private void handleAddCategory() {
        String path = categoryPathField.getText().trim();
        String nome = categoryNameField.getText().trim();
        if (path.isEmpty() || nome.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Inserisci un nome per la categoria.");
            return;
        }

        try {
            CategoryDAO.getInstance().addCategory(path, nome);
            showAlert(Alert.AlertType.INFORMATION, "Categoria '" + nome + "' aggiunta con successo!");
            categoryPathField.clear();
            categoryNameField.clear();
        } catch (DAOException e) {
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
            ReportBean report = ReportDAO.getInstance().generateReport(username);

            if (report != null) {
                reportOutputArea.setText(report.toString());
            } else {
                reportOutputArea.setText("Nessun dato trovato per l'utente " + username);
            }

        } catch (DAOException e) {
            reportOutputArea.setText("Errore: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().setLoggedUser(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) categoryNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}