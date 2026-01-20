package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ChatController {

    @FXML private Label headerLabel;
    @FXML private ListView<MessageBean> messageListView;
    @FXML private TextArea inputArea;
    @FXML private Button backButton;

    private String myUsername;
    private String otherUsername;

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goBack());
        }

        // Imposto una "CellFactory" per personalizzare l'aspetto della lista
        messageListView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(MessageBean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Se il mittente sono io (utente loggato)
                    if (item.getMittente().equals(myUsername)) {
                        setText(item.getTesto() + " (Io)" + "\n " + item.getOra().toString() + " " + item.getData().toString());
                        // Allineato a DESTRA, Blu
                        setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    } else {
                        // Se il mittente è l'altro
                        setText(item.getTesto() + " (" + item.getMittente() + ")" + "\n " + item.getOra().toString() + " " + item.getData().toString());
                        // Allineato a SINISTRA, Nero
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: black;");
                    }
                }
            }
        });
    }

    // Metodo fondamentale per passare i dati da AdPage a qui
    public void initData(String recipientUsername) {
        this.myUsername = Session.getInstance().getLoggedUser().getUsername();
        this.otherUsername = recipientUsername;

        headerLabel.setText("Conversazione con " + otherUsername);

        loadMessages();
    }

    private void loadMessages() {
        try {
            List<MessageBean> msgs = MessageDAO.getInstance().retrieveMessages(myUsername, otherUsername);
            messageListView.setItems(FXCollections.observableArrayList(msgs));
            // Scorrere in fondo
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
            // Usa il DAO esistente per inviare
            MessageDAO.getInstance().inviaMessaggio(myUsername, otherUsername, text);

            // Pulisci input e ricarica la lista per vedere il nuovo messaggio
            inputArea.clear();
            loadMessages();

        } catch (DAOException e) {
            showAlert("Errore invio: " + e.getMessage());
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
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