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
        System.out.println("   1) Demo (In-Memory - Volatile)");
        System.out.println("   2) File System (Persistente su file)");
        System.out.println("   3) MySQL (Database)");
        System.out.print("   Scelta: ");

        String persistenceInput = scanner.nextLine().trim();

        switch (persistenceInput) {
            case "3":
                Config.mode = Config.PersistenceMode.MYSQL;
                System.out.println("   >> Modalità impostata: MySQL");
                break;
            case "2":
                Config.mode = Config.PersistenceMode.FILE_SYSTEM;
                System.out.println("   >> Modalità impostata: File System");
                break;
            case "1":
                Config.mode = Config.PersistenceMode.IN_MEMORY;
                System.out.println("   >> Modalità impostata: Demo (In-Memory)");
                break;
            default:
                System.out.println("   >> Input non valido. Impostazione di default: Demo (In-Memory)");
                Config.mode = Config.PersistenceMode.IN_MEMORY;
                break;
        }

        // --- STEP 2: Scelta Interfaccia ---
        System.out.println("\n[2] Seleziona l'interfaccia utente:");
        System.out.println("   1) GUI (Interfaccia Grafica JavaFX)");
        System.out.println("   2) CLI (Riga di Comando)");
        System.out.print("   Scelta: ");

        String viewInput = scanner.nextLine();

        if (viewInput.trim().equals("2")) {
            System.out.println("\n   >> Avvio CLI in corso...\n");
            new CLIView().run();
        } else if (viewInput.trim().equals("1")) {
            System.out.println("\n   >> Avvio GUI in corso...");
            launch();
        } else {
            System.out.println("   >> Input non valido. Uscita dal programma.");
            System.exit(0);
        }
    }
}