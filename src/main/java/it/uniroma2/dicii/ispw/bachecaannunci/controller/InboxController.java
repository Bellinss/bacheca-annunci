package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class InboxController {

    @FXML private ListView<String> usersList;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goHome());
        }
        loadConversations();
    }

    private void loadConversations() {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) return;

        try {
            // Chiama il DAO per avere la lista di nomi (es. "MarioRossi", "LucaBianchi")
            List<String> chats = MessageDAO.getInstance().getActiveConversations(user.getUsername());

            usersList.getItems().clear();
            usersList.getItems().addAll(chats);

            if(chats.isEmpty()) {
                usersList.setPlaceholder(new javafx.scene.control.Label("Nessuna conversazione attiva."));
            }

        } catch (DAOException e) {
            new Alert(Alert.AlertType.ERROR, "Errore: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleOpenChat() {
        // 1. Prendi l'elemento selezionato
        String selectedUser = usersList.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return; // Click a vuoto

        try {
            // 2. Carica la scena della Chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            // 3. Passa il nome dell'interlocutore al ChatController
            ChatController controller = loader.getController();
            controller.initData(selectedUser);

            // 4. Cambia scena
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}