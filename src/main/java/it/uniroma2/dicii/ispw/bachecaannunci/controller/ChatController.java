package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.ChatAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ChatController {

    @FXML private Label headerLabel;
    @FXML private ListView<MessageBean> messageListView;
    @FXML private TextArea inputArea;
    @FXML private Button backButton;

    // Riferimento al Controller Applicativo
    private final ChatAppController appController = new ChatAppController();

    // Stato della View
    private String otherUsername;
    private String myUsername; // Serve per la grafica (allineamento messaggi)

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goBack());
        }

        // Recuperiamo subito chi siamo per gestire l'allineamento nella lista
        this.myUsername = appController.getLoggedUsername();

        // Configurazione CellFactory per visualizzare i messaggi (Grafica pura)
        messageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessageBean msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Creazione Layout Grafico del singolo messaggio
                    VBox bubble = new VBox(5);
                    Label msgText = new Label(msg.getTesto());
                    msgText.setWrapText(true);
                    msgText.setMaxWidth(250);

                    Label dateLabel = new Label(msg.getData().toString());
                    dateLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: gray;");

                    bubble.getChildren().addAll(msgText, dateLabel);

                    // Logica di visualizzazione: Io a destra, Altri a sinistra
                    boolean isMe = msg.getMittente().equals(myUsername);

                    if (isMe) {
                        bubble.setStyle("-fx-background-color: #dcf8c6; -fx-padding: 10; -fx-background-radius: 10;");
                        HBox container = new HBox(bubble);
                        container.setAlignment(Pos.CENTER_RIGHT);
                        setGraphic(container);
                    } else {
                        bubble.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");
                        HBox container = new HBox(bubble);
                        container.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(container);
                    }
                }
            }
        });
    }

    // Metodo chiamato da chi apre la chat (es. AdPageController)
    public void initChat(String seller, int adId, String adTitle) {
        this.otherUsername = seller;
        if (headerLabel != null) {
            headerLabel.setText("Chat con " + seller + " - " + adTitle);
        }
        loadMessages();
    }

    private void loadMessages() {
        try {
            // Delega all'AppController il recupero dati
            List<MessageBean> msgs = appController.getMessages(otherUsername);

            messageListView.setItems(FXCollections.observableArrayList(msgs));

            // Scroll automatico in basso
            if (!msgs.isEmpty()) {
                messageListView.scrollTo(msgs.size() - 1);
            }
        } catch (DAOException e) {
            showAlert("Errore caricamento chat: " + e.getMessage());
        }
    }

    @FXML
    private void handleSend() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) return;

        try {
            // Delega all'AppController l'invio
            appController.sendMessage(otherUsername, text);

            // Aggiorna UI
            inputArea.clear();
            loadMessages();

        } catch (DAOException e) {
            showAlert("Errore invio: " + e.getMessage());
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml")); // o adPage.fxml
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}