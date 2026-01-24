package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.RegistrationAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegistrationController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField residenzaField;
    @FXML private TextField fatturazioneField;
    @FXML private TextField recapitoField;

    @FXML private RadioButton rbEmail;
    @FXML private RadioButton rbCellulare;
    private ToggleGroup tipoRecapitoGroup;

    @FXML private Button registerButton;
    @FXML private Button backButton;

    // Riferimento al Controller Applicativo
    private final RegistrationAppController appController = new RegistrationAppController();

    @FXML
    public void initialize() {
        // Setup Grafico: ToggleGroup per i RadioButton
        tipoRecapitoGroup = new ToggleGroup();
        if (rbEmail != null && rbCellulare != null) {
            rbEmail.setToggleGroup(tipoRecapitoGroup);
            rbCellulare.setToggleGroup(tipoRecapitoGroup);
            rbEmail.setSelected(true); // Default
        }

        // 2. Setup Bottone "Torna al Login" (FIX)
        if (backButton != null) {
            backButton.setOnAction(e -> goToLogin());
        }
    }

    @FXML
    private void handleRegister() {
        // 1. Recupero dati dalla GUI
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String dataStr = dataNascitaField.getText();
        String residenza = residenzaField.getText();
        String fatturazione = fatturazioneField.getText(); // Opzionale
        String recapito = recapitoField.getText();

        // Determina tipo recapito
        String tipo = (rbEmail != null && rbEmail.isSelected()) ? "email" : "cellulare";

        // 2. Validazione Sintattica (Campi obbligatori)
        if (user.isBlank() || pass.isBlank() || nome.isBlank() || cognome.isBlank() ||
                dataStr.isBlank() || residenza.isBlank() || recapito.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Compila tutti i campi obbligatori!");
            return;
        }

        try {
            // 3. Conversione Data (Logica di presentazione)
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // 4. Creazione Bean per il passaggio dati
            UserBean bean = new UserBean(user, pass, nome, cognome, java.sql.Date.valueOf(data), residenza, fatturazione, tipo, recapito);

            // 5. Chiamata al Controller Applicativo
            boolean success = appController.registerUser(bean);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registrazione avvenuta con successo!");
                goToLogin();
            }

        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Formato data non valido. Usa dd/MM/yyyy");
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore durante la registrazione: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (registerButton != null ? registerButton : usernameField).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Impossibile tornare al login.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}