package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.util.Scanner;

public class CLIView {

    private final Scanner scanner;

    // Sotto-View
    private final CLIAccountView accountView;
    private final CLIHomeView homeView;
    private final CLIAdminView adminView;

    public CLIView() {
        this.scanner = new Scanner(System.in);
        // Passiamo lo scanner alle sotto-view per condividerlo
        this.accountView = new CLIAccountView(scanner);
        this.homeView = new CLIHomeView(scanner);
        this.adminView = new CLIAdminView(scanner);
    }

    public void run() {
        System.out.println("=========================================");
        System.out.println("   BACHECA ANNUNCI - CLI MODE (" + (Config.IS_DEMO_VERSION ? "DEMO" : "FULL") + ")");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            Credentials user = Session.getInstance().getLoggedUser();

            if (user == null) {
                // Utente non loggato -> Gestione Login/Registrazione
                boolean exit = accountView.runGuestMenu();
                if (exit) running = false;
            } else {
                // Utente loggato -> Smistamento per Ruolo
                if (user.getRole() == Role.AMMINISTRATORE) {
                    adminView.run();
                } else {
                    homeView.run();
                }
            }
        }

        System.out.println("Chiusura applicazione...");
        System.exit(0);
    }
}