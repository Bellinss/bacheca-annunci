package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.RegistrationProcedureDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.io.IOException;
import java.time.LocalDate;

public class RegistrationController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nomeField;
    @FXML private TextField cognomeField;
    @FXML private TextField dataNascitaField;
    @FXML private TextField residenzaField;
    @FXML private TextField fatturazioneField;
    @FXML private TextField recapitoField;

    // Per scegliere tra Email e Cellulare
    @FXML private RadioButton rbEmail;
    @FXML private RadioButton rbCellulare;
    private ToggleGroup tipoRecapitoGroup;

    @FXML private Button registerButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // Raggruppa i RadioButton per permetterne solo uno alla volta
        tipoRecapitoGroup = new ToggleGroup();
        if(rbEmail != null) rbEmail.setToggleGroup(tipoRecapitoGroup);
        if(rbCellulare != null) rbCellulare.setToggleGroup(tipoRecapitoGroup);

        // Default select
        if(rbEmail != null) rbEmail.setSelected(true);

        if (backButton != null) {
            backButton.setOnAction(e -> goToLogin());
        }
    }

    @FXML
    private void handleRegister() {
        // 1. Recupera i dati
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        LocalDate data = null;
        try {
            // Definisci il formato atteso (giorno/mese/anno)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = LocalDate.parse(dataNascitaField.getText(), formatter);
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.WARNING, "Data non valida! Usa il formato dd/mm/yyyy (es. 25/12/2000)");
            return;
        }
        String residenza = residenzaField.getText();
        String fatturazione = fatturazioneField.getText();
        String recapito = recapitoField.getText();

        String tipo = (rbEmail != null && rbEmail.isSelected()) ? "email" : "cellulare";

        // 2. Validazione basilare lato client
        if (user.isBlank() || pass.isBlank() || nome.isBlank() || cognome.isBlank() ||
                data == null || residenza.isBlank() || recapito.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Compila tutti i campi obbligatori!");
            return;
        }

        // 3. Crea il Bean
        UserBean bean = new UserBean(user, pass, nome, cognome, data, residenza, fatturazione, tipo, recapito);

        // 4. Chiama il DAO
        try {
            boolean success = RegistrationProcedureDAO.getInstance().execute(bean);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registrazione avvenuta con successo!");
                goToLogin();
            }
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore: " + e.getMessage());
        }
    }

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}