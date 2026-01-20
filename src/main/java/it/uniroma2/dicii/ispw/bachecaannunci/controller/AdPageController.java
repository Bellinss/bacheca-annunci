package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AdPageController {

    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label categoryLabel;

    // Bottoni di navigazione e azione
    @FXML private Button backButton;
    @FXML private Button followButton;
    @FXML private Button contactButton;
    @FXML private Button soldButton; // Tasto "Segna come Venduto"

    private AnnuncioBean currentAnnuncio;

    @FXML
    public void initialize() {
        // Collega l'azione del tasto indietro
        if (backButton != null) {
            backButton.setOnAction(e -> goBack());
        }
    }

    public void setAnnuncio(AnnuncioBean annuncio) {
        this.currentAnnuncio = annuncio;

        // 1. Imposta i testi nella UI
        if (titleLabel != null) titleLabel.setText(annuncio.getTitolo());
        if (priceLabel != null) priceLabel.setText(String.format("€ %.2f", annuncio.getImporto()));
        if (descriptionArea != null) descriptionArea.setText(annuncio.getDescrizione());

        if (categoryLabel != null) {
            categoryLabel.setText(annuncio.getCategoria().toUpperCase());
        }

        // 2. Recupera l'utente loggato
        Credentials user = Session.getInstance().getLoggedUser();

        // 3. Logica Bottoni
        if (user != null) {
            String myUsername = user.getUsername();
            String sellerUsername = annuncio.getVenditore();

            // CASO A: SONO IL PROPRIETARIO (VENDITORE)
            if (myUsername.equals(sellerUsername)) {
                // Nascondo tasti per acquirenti
                setButtonVisible(contactButton, false);
                setButtonVisible(followButton, false);

                // Mostro tasto per vendere
                setButtonVisible(soldButton, true);
            }
            // CASO B: SONO UN ACQUIRENTE
            else {
                // Nascondo tasto per vendere
                setButtonVisible(soldButton, false);

                // Mostro tasti azione
                setButtonVisible(contactButton, true);
                setButtonVisible(followButton, true);

                // Controllo se seguo già l'annuncio
                checkIfFollowing(myUsername, annuncio.getId());
            }
        } else {
            // CASO C: UTENTE NON LOGGATO
            setButtonVisible(soldButton, false);
            // Lasciamo visibili gli altri ma daranno errore al click chiedendo il login
        }
    }

    // --- GESTIONE AZIONI ---

    @FXML
    private void handleFollow() {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) {
            showAlert(Alert.AlertType.WARNING, "Devi effettuare il login per seguire gli annunci.");
            return;
        }

        try {
            boolean success = AdDAO.getInstance().seguiAnnuncio(currentAnnuncio.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Annuncio aggiunto ai preferiti!");
                followButton.setText("Seguito ✓");
                followButton.setDisable(true);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Stai già seguendo questo annuncio.");
            }
        } catch (DAOException e) {
            if (e.getMessage().contains("venduto") || e.getMessage().contains("rimosso")) {
                new Alert(Alert.AlertType.ERROR, "Impossibile seguire: L'annuncio è stato venduto o rimosso.").showAndWait();
                goBack();
            } else {
                new Alert(Alert.AlertType.ERROR, "Errore: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    private void handleContactSeller() {
        Credentials user = Session.getInstance().getLoggedUser();
        if (user == null) {
            showAlert(Alert.AlertType.WARNING, "Devi effettuare il login per contattare il venditore.");
            return;
        }

        try {
            // Carica la schermata della Chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            // Passa lo username del venditore al ChatController
            ChatController controller = loader.getController();
            controller.initData(currentAnnuncio.getVenditore());

            // Cambia scena
            Stage stage = (Stage) contactButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Errore nell'apertura della chat.");
        }
    }

    @FXML
    private void handleMarkAsSold() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Vendita");
        alert.setHeaderText("Segnare l'annuncio come VENDUTO?");
        alert.setContentText("L'annuncio verrà rimosso dalla lista pubblica.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Credentials user = Session.getInstance().getLoggedUser();
                // Chiama il DAO per aggiornare lo stato
                AdDAO.getInstance().markAsSold(currentAnnuncio.getId(), user.getUsername());

                new Alert(Alert.AlertType.INFORMATION, "Annuncio venduto con successo!").showAndWait();

                // Torna alla Home per aggiornare la lista
                goBack();

            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Errore: " + e.getMessage());
            }
        }
    }

    // --- METODI DI SUPPORTO ---

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) soldButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIfFollowing(String username, int idAnnuncio) {
        try {
            boolean isFollowing = AdDAO.getInstance().isFollowing(username, idAnnuncio);
            if (isFollowing) {
                followButton.setText("Seguito ✓");
                followButton.setDisable(true);
            }
        } catch (DAOException e) {
            e.printStackTrace(); // Non bloccante
        }
    }

    private void setButtonVisible(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible); // Se nascosto, non occupa spazio nel layout
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}