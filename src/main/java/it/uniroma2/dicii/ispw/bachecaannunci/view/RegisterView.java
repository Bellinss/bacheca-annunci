package it.uniroma2.dicii.ispw.bachecaannunci.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.io.IOException;

public class RegisterView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = RegisterView.class.getResource("/register.fxml");
        if (fxmlUrl == null) throw new IOException("FXML resource '/register.fxml' non trovato nel classpath");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        stage.setTitle("Bacheca Elettronica di Annunci");
        stage.setScene(new Scene(root));
        stage.show();
    }
}