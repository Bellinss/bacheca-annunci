package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.RegistrationAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.ValidationException;
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
import java.util.regex.Pattern;

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

    private final RegistrationAppController appController = new RegistrationAppController();
    private static final String NAME_REGEX = "^[a-zA-Z\\s']+$";

    @FXML
    public void initialize() {
        tipoRecapitoGroup = new ToggleGroup();
        if (rbEmail != null && rbCellulare != null) {
            rbEmail.setToggleGroup(tipoRecapitoGroup);
            rbCellulare.setToggleGroup(tipoRecapitoGroup);
            rbEmail.setSelected(true);
        }
        if (backButton != null) {
            backButton.setOnAction(e -> goToLogin());
        }
    }

    @FXML
    private void handleRegister() {
        // Recupero dati grezzi dalla UI
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String nome = capitalize(nomeField.getText());
        String cognome = capitalize(cognomeField.getText());
        String dataStr = dataNascitaField.getText();
        String residenza = capitalize(residenzaField.getText());
        String fatturazione = capitalize(fatturazioneField.getText());
        String recapito = recapitoField.getText();
        String tipo = (rbEmail != null && rbEmail.isSelected()) ? "email" : "cellulare";

        // Regex check basilari per la UI
        if (!Pattern.matches(NAME_REGEX, nome)) {
            showAlert(Alert.AlertType.ERROR, "Il Nome contiene caratteri non validi.");
            return;
        }
        if (!Pattern.matches(NAME_REGEX, cognome)) {
            showAlert(Alert.AlertType.ERROR, "Il Cognome contiene caratteri non validi.");
            return;
        }

        try {
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            UserBean bean = new UserBean(user, pass, nome, cognome, java.sql.Date.valueOf(data), residenza, fatturazione, tipo, recapito);

            // --- CHIAMATA AL CONTROLLER CON GESTIONE ECCEZIONI ---
            if (appController.registerUser(bean)) {
                showAlert(Alert.AlertType.INFORMATION, "Registrazione completata!");
                goToLogin();
            }

        } catch (ValidationException e) {
            // GESTIONE 1: Errore di validazione (es. password corta) -> Warning all'utente
            showAlert(Alert.AlertType.WARNING, "Attenzione: " + e.getMessage());

        } catch (DAOException e) {
            // GESTIONE 2: Errore tecnico (es. DB offline, user duplicato) -> Error
            showAlert(Alert.AlertType.ERROR, "Errore sistema: " + e.getMessage());

        } catch (DateTimeParseException e) {
            // GESTIONE 3: Errore formato data -> Error
            showAlert(Alert.AlertType.ERROR, "Formato data non valido. Usa dd/MM/yyyy");
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isBlank()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
            showAlert(Alert.AlertType.ERROR, "Impossibile tornare alla schermata di login.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}