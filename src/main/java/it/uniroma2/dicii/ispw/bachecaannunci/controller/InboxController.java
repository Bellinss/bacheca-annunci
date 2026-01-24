package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.InboxAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class InboxController {

    @FXML private ListView<String> usersList;
    @FXML private Button backButton;

    // Riferimento al Controller Applicativo
    private final InboxAppController appController = new InboxAppController();

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goHome());
        }
        loadConversations();
    }

    private void loadConversations() {
        try {
            // Delega all'AppController il recupero dei dati
            List<String> chats = appController.getActiveConversations();

            // Aggiorna la GUI
            usersList.getItems().clear();
            usersList.getItems().addAll(chats);

            if (chats.isEmpty()) {
                usersList.setPlaceholder(new Label("Nessuna conversazione attiva."));
            }

        } catch (DAOException e) {
            showAlert("Errore caricamento conversazioni: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenChat() {
        // 1. Prendi l'elemento selezionato dalla GUI
        String selectedUser = usersList.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return; // Click a vuoto

        try {
            // 2. Carica la scena della Chat (Navigazione Grafica)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            // 3. Inizializza il controller di destinazione
            ChatController controller = loader.getController();
            // Nota: qui passiamo solo lo username e 0 come ID annuncio generico per chat libera,
            // oppure adatta initChat se necessario.
            controller.initChat(selectedUser, 0, "Conversazione");

            // 4. Cambia scena
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            showAlert("Errore apertura chat: " + e.getMessage());
        }
    }

    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Errore ritorno alla home: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}