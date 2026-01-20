package it.uniroma2.dicii.ispw.bachecaannunci.controller;

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

    @FXML
    private VBox itemBox;

    @FXML
    private ImageView imgView;

    @FXML
    private Label nameLabel;  // Deve coincidere con fx:id nel FXML

    @FXML
    private Label priceLabel; // Deve coincidere con fx:id nel FXML

    private AnnuncioBean annuncio;

    public void setData(AnnuncioBean annuncio) {
        this.annuncio = annuncio;

        if (nameLabel != null) {
            nameLabel.setText(annuncio.getTitolo());
        } else {
            System.out.println("ERRORE: nameLabel è NULL! Controlla fx:id in item.fxml");
        }

        if (priceLabel != null) {
            priceLabel.setText(String.format("€ %.2f", annuncio.getImporto()));
        } else {
            System.out.println("ERRORE: priceLabel è NULL! Controlla fx:id in item.fxml");
        }
    }

    @FXML
    private void handleItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adPage.fxml"));
            Parent root = loader.load();

            AdPageController controller = loader.getController();
            controller.setAnnuncio(this.annuncio);

            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}