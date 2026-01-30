package it.uniroma2.dicii.ispw.bachecaannunci;

import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;
import it.uniroma2.dicii.ispw.bachecaannunci.view.CLIView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Il metodo start viene chiamato solo se l'utente ha scelto la GUI nel main.
        // La configurazione (Config.IS_DEMO_VERSION) è già stata impostata nel main.

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bacheca Elettronica di Annunci");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("******************************************");
        System.out.println("*      BACHECA ANNUNCI - LAUNCHER        *");
        System.out.println("******************************************");

        // --- STEP 1: Scelta Persistenza ---
        System.out.println("\n[1] Seleziona la modalità di salvataggio dati:");
        System.out.println("   1) File System (Demo)");
        System.out.println("   2) MySQL (Database)");
        System.out.print("   Scelta: ");

        String persistenceInput = scanner.nextLine();

        if (persistenceInput.trim().equals("2")) {
            Config.IS_DEMO_VERSION = false;
            System.out.println("   >> Modalità impostata: MySQL");
        } else {
            Config.IS_DEMO_VERSION = true;
            System.out.println("   >> Modalità impostata: File System");
        }

        // --- STEP 2: Scelta Interfaccia ---
        System.out.println("\n[2] Seleziona l'interfaccia utente:");
        System.out.println("   1) GUI (Interfaccia Grafica JavaFX)");
        System.out.println("   2) CLI (Riga di Comando)");
        System.out.print("   Scelta: ");

        String viewInput = scanner.nextLine();

        if (viewInput.trim().equals("2")) {
            // Avvio modalità CLI
            System.out.println("\n   >> Avvio CLI in corso...\n");
            // Non chiudiamo lo scanner qui perché la CLI potrebbe usarlo (System.in)
            new CLIView().run();
        } else {
            // Avvio modalità GUI
            System.out.println("\n   >> Avvio GUI in corso...");
            launch();
        }
    }
}