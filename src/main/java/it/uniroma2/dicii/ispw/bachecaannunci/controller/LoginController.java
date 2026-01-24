package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.LoginAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private Button loginButton;

    // Riferimento al Controller Applicativo
    private final LoginAppController appController = new LoginAppController();

    @FXML
    private void initialize() {
        // Permette di premere Invio per fare il login
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // 1. Validazione Grafica
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Inserisci username e password");
            return;
        }

        try {
            // 2. Delega all'AppController
            Credentials cred = appController.login(username, password);

            if (cred == null) {
                showAlert(Alert.AlertType.ERROR, "Credenziali errate. Username o password non valide.");
                passwordTextField.clear();
                return;
            }

            // 3. Navigazione in base al Ruolo (Logica di presentazione)
            String targetFxml = "/home.fxml"; // Default: Utente
            if (cred.getRole() == Role.AMMINISTRATORE) {
                targetFxml = "/adminHome.fxml";
            }

            loadScene(targetFxml);

        } catch (DAOException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Errore login: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento pagina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() {
        try {
            loadScene("/register.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Impossibile aprire la registrazione.");
        }
    }

    private void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}