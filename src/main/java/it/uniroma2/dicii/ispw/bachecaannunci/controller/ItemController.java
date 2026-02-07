package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.ItemAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemController {

    @FXML private VBox itemBox;
    @FXML private ImageView imgView;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;

    private AnnuncioBean annuncio;

    // Riferimento al Controller Applicativo
    private final ItemAppController appController = new ItemAppController();

    public void setData(AnnuncioBean annuncio) {
        this.annuncio = annuncio;

        // Impostazione dati UI
        if (nameLabel != null) {
            nameLabel.setText(annuncio.getTitolo());
        }

        if (priceLabel != null) {
            // Delega la formattazione all'AppController
            priceLabel.setText(appController.formatPrice(annuncio.getImporto()));
        }
    }

    @FXML
    private void handleItemClick() {
        try {
            // 1. Caricamento della Scena di Dettaglio (AdPage)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adPage.fxml"));
            Parent root = loader.load();

            // 2. Passaggio dati al controller di destinazione
            AdPageController controller = loader.getController();
            controller.setAnnuncio(this.annuncio);

            // 3. Cambio Scena
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            System.err.println("Errore apertura annuncio: " + e.getMessage());
        }
    }
}