package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.AdPageAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.CommentAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.NoteAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.util.List;
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
    @FXML private ListView<CommentBean> commentsListView;
    @FXML private TextField commentInput;

    // Contenitore principale delle note (contiene lista e input)
    @FXML private VBox notesContainer;
    @FXML private ListView<NoteBean> notesListView;
    @FXML private TextField noteInput;

    // Bottoni
    @FXML private Button backButton;
    @FXML private Button followButton;
    @FXML private Button contactButton;
    @FXML private Button soldButton;

    private AnnuncioBean currentAnnuncio;

    private final AdPageAppController appController = new AdPageAppController();
    private final CommentAppController commentAppController = new CommentAppController();
    private final NoteAppController noteAppController = new NoteAppController();

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(e -> goBack());
        }
    }

    public void setAnnuncio(AnnuncioBean annuncio) {
        this.currentAnnuncio = annuncio;

        // 1. Aggiorna UI con i dati
        if (titleLabel != null) titleLabel.setText(annuncio.getTitolo());
        if (priceLabel != null) priceLabel.setText(String.format("€ %.2f", annuncio.getImporto()));
        if (descriptionArea != null) descriptionArea.setText(annuncio.getDescrizione());
        if (categoryLabel != null && annuncio.getCategoria() != null) {
            categoryLabel.setText(annuncio.getCategoria().toUpperCase());
        }

        boolean loggedIn = appController.isLoggedIn();

        if (!loggedIn) {
            // Se non loggato, nascondi tutto ciò che è interattivo
            setButtonVisible(soldButton, false);
            setButtonVisible(followButton, false);
            setButtonVisible(contactButton, false);
            setNotesVisible(false); // Nascondi note
        } else {
            // Controlla se è il proprietario
            boolean isOwner = appController.isOwner(annuncio.getVenditore());

            if (isOwner) {
                setNotesVisible(true);
                loadNotes();
            } else {
                setNotesVisible(false);
            }

            // --- LOGICA BOTTONI ---
            if (isOwner) {
                setButtonVisible(soldButton, true);
                setButtonVisible(followButton, false);
                setButtonVisible(contactButton, false);
            } else {
                setButtonVisible(soldButton, false);
                setButtonVisible(followButton, true);
                setButtonVisible(contactButton, true);
                checkIfFollowing();
            }
        }
        loadComments();
    }

    // Metodo helper per nascondere l'intero blocco note
    private void setNotesVisible(boolean visible) {
        if (notesContainer != null) {
            notesContainer.setVisible(visible);
            notesContainer.setManaged(visible); // Rimuove lo spazio occupato se nascosto
        }
    }

    private void loadComments() {
        try {
            List<CommentBean> comments = commentAppController.getComments(currentAnnuncio.getId());
            commentsListView.getItems().clear();
            commentsListView.getItems().addAll(comments);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore caricamento commenti: " + e.getMessage());
        }
    }

    @FXML
    private void handleFollow() {
        try {
            boolean result = appController.followAd(currentAnnuncio.getId());
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Annuncio aggiunto ai preferiti!");
                followButton.setText("Seguito ✓");
                followButton.setDisable(true);
            }
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Following: " + e.getMessage());
        }
    }

    @FXML
    private void handlePostComment() {
        String text = commentInput.getText();
        try {
            commentAppController.postComment(text, currentAnnuncio.getId());
            commentInput.clear();
            loadComments();
        } catch (DAOException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
        }
    }

    private void loadNotes() {
        try {
            List<NoteBean> notes = noteAppController.getNotes(currentAnnuncio.getId());
            notesListView.getItems().clear();
            notesListView.getItems().addAll(notes);
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore note: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddNote() {
        String text = noteInput.getText();
        try {
            noteAppController.addNote(text, currentAnnuncio.getId());
            noteInput.clear();
            loadNotes();
            new Alert(Alert.AlertType.INFORMATION, "Nota salvata!").showAndWait();
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void handleMarkAsSold() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Vendita");
        alert.setHeaderText("Vuoi segnare questo oggetto come VENDUTO?");
        alert.setContentText("L'annuncio non sarà più visibile nella bacheca.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                appController.markAsSold(currentAnnuncio.getId());
                new Alert(Alert.AlertType.INFORMATION, "Annuncio venduto con successo!").showAndWait();
                goBack();
            } catch (DAOException e) {
                showAlert(Alert.AlertType.ERROR, "Errore vendita: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleContact() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();
            ChatController chatController = loader.getController();
            chatController.initChat(currentAnnuncio.getVenditore(), currentAnnuncio.getTitolo());
            Stage stage = (Stage) contactButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore apertura chat: " + e.getMessage());
        }
    }

    private void checkIfFollowing() {
        try {
            boolean isFollowing = appController.isFollowing(currentAnnuncio.getId());
            if (isFollowing) {
                followButton.setText("Seguito ✓");
                followButton.setDisable(true);
            }
        } catch (DAOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore: " + e.getMessage());
        }
    }

    private void setButtonVisible(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (backButton != null ? backButton : titleLabel).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Errore Navigazione: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}