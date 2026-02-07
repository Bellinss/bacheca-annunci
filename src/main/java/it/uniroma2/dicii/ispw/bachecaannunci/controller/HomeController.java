package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.HomeAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

public class HomeController implements Initializable {

    @FXML private TilePane adsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private CheckBox onlyFollowedCheckBox;

    // Riferimento al Controller Applicativo
    private final HomeAppController appController = new HomeAppController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 1. Carica le categorie tramite AppController
            categoryComboBox.getItems().add("Tutte le categorie");
            categoryComboBox.getItems().addAll(appController.getCategories());
            categoryComboBox.getSelectionModel().selectFirst();

            // 2. Carica gli annunci iniziali
            populateGrid(appController.getAllAds());

        } catch (DAOException e) {
            showError("Errore durante l'inizializzazione: " + e.getMessage());
        }
    }

    @FXML
    private void handleApplyFilters() {
        try {
            // Delega la logica di filtro all'AppController
            List<AnnuncioBean> risultati = appController.filterAds(
                    categoryComboBox.getValue(),
                    searchField.getText().trim(),
                    onlyFollowedCheckBox.isSelected()
            );

            // Aggiorna la vista
            populateGrid(risultati);

            if (risultati.isEmpty()) {
                showInfo("Nessun annuncio trovato con questi criteri.");
            }

        } catch (DAOException e) {
            // Gestione specifica se l'utente non è loggato ma ha chiesto i preferiti
            if (e.getMessage().contains("login")) {
                onlyFollowedCheckBox.setSelected(false);
                showError(e.getMessage());
            } else {
                showError("Errore nel filtraggio: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleShowNotifications() {
        if (!appController.isUserLogged()) {
            showError("Devi effettuare il login per vedere le notifiche.");
            return;
        }

        try {
            // 1. Recupera notifiche dall'AppController
            List<NotificationBean> notifications = appController.getNotifications();

            if (notifications.isEmpty()) {
                showInfo("Non hai nuove notifiche.");
                return;
            }

            // 2. Costruisci il messaggio da mostrare
            StringBuilder content = new StringBuilder();
            for (NotificationBean n : notifications) {
                content.append("• ").append(n.toString()).append("\n\n");
            }

            // 3. Mostra un Alert con opzione "Cancella Tutto"
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Le tue Notifiche");
            alert.setHeaderText("Aggiornamenti Annunci Seguiti");
            alert.setContentText(content.toString());

            // Aggiungiamo un bottone custom per pulire le notifiche
            ButtonType clearBtn = new ButtonType("Segna come lette (Cancella)");
            ButtonType closeBtn = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(clearBtn, closeBtn);

            Optional<ButtonType> result = alert.showAndWait();

            // 4. Se l'utente preme "Cancella", chiamiamo l'AppController
            if (result.isPresent() && result.get() == clearBtn) {
                appController.clearAllNotifications();
                showInfo("Notifiche cancellate.");
            }

        } catch (DAOException e) {
            showError("Errore caricamento notifiche: " + e.getMessage());
        }
    }

    @FXML
    private void goToSell() {
        // Usa l'AppController per verificare il login
        if (!appController.isUserLogged()) {
            showError("Devi fare il login per vendere un oggetto.");
            return;
        }
        changeScene("/sell.fxml");
    }

    @FXML
    private void goToInbox() {
        if (!appController.isUserLogged()) {
            showError("Devi fare il login per vedere i messaggi.");
            return;
        }
        changeScene("/inbox.fxml");
    }

    @FXML
    private void handleLogout() {
        appController.logout();
        changeScene("/login.fxml");
    }

    // --- Metodi puramente grafici ---

    private void populateGrid(List<AnnuncioBean> annunci) {
        adsContainer.getChildren().clear();
        try {
            for (AnnuncioBean annuncio : annunci) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/item.fxml"));
                VBox cardBox = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(annuncio);

                adsContainer.getChildren().add(cardBox);
            }
        } catch (IOException e) {
            showError("Errore nel caricamento delle card annuncio.");
        }
    }

    private void changeScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) adsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Impossibile caricare la pagina: " + fxmlFile);
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}