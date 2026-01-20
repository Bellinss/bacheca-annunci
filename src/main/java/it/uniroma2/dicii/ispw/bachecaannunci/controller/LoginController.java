package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.LoginProcedureDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ConnectionFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;

import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        // Puoi usare il tasto Invio per fare il login
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // 1. Validazione Input
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Inserisci username e password").showAndWait();
            return;
        }

        try {
            // 2. Chiamata al Database (UNA SOLA VOLTA!)
            Credentials result = LoginProcedureDAO.getInstance().execute(username, password);

            if (result == null) {
                new Alert(Alert.AlertType.ERROR, "Credenziali errate. Riprova.").showAndWait();
                passwordTextField.clear();
                return;
            }

            // 3. Cambio Ruolo Database (Switch tra utente 'login', 'utente', 'amministratore' su MySQL)
            ConnectionFactory.changeRole(result.getRole());

            // 4. Salvataggio Sessione
            Session.getInstance().setLoggedUser(result);

            // 5. Reindirizzamento in base al Ruolo
            String targetFxml = "/home.fxml"; // Default: Utente (Role = 2)

            // Assumo che getRole() ritorni un oggetto Role che ha un metodo getId()
            // 1 = Amministratore, 2 = Utente
            if (result.getRole() == Role.AMMINISTRATORE) {
                targetFxml = "/adminHome.fxml";
            }

            // 6. Cambio Scena
            loadScene(targetFxml);

        } catch (DAOException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Errore Login: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore caricamento interfaccia: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    private void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void goToRegister() {
        try {
            loadScene("/register.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
