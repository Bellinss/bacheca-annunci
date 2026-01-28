package it.uniroma2.dicii.ispw.bachecaannunci;

import it.uniroma2.dicii.ispw.bachecaannunci.view.CLIView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bacheca Elettronica di Annunci");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Controllo argomenti da riga di comando
        if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            // Avvia modalità CLI
            System.out.println("Avvio in modalità CLI (Command Line Interface)...");
            new CLIView().run();
        } else {
            // Avvia modalità GUI (JavaFX)
            launch();
        }
    }
}