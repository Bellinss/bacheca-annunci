package it.uniroma2.dicii.ispw.bachecaannunci.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = LoginView.class.getResource("/login.fxml");
        if (fxmlUrl == null) {
            throw new IOException("FXML resource '/login.fxml' not found on classpath");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setTitle("Bacheca Elettronica di Annunci");
        stage.setScene(scene);
        stage.show();
    }
}