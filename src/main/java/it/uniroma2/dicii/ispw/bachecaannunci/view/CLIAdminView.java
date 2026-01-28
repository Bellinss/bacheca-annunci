package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.AdminAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;

import java.util.Scanner;

public class CLIAdminView {
    private final Scanner scanner;
    private final AdminAppController adminController = new AdminAppController();

    public CLIAdminView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("\n--- MENU ADMIN ---");
        System.out.println("1. Aggiungi Categoria");
        System.out.println("2. Genera Report");
        System.out.println("9. Logout");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("Nome nuova categoria: ");
                try {
                    adminController.addCategory("icon_path", scanner.nextLine());
                    System.out.println("Fatto.");
                } catch (DAOException e) { System.out.println("Errore: " + e.getMessage()); }
                break;
            case "2":
                System.out.print("Username target: ");
                try {
                    ReportBean rb = adminController.generateUserReport(scanner.nextLine());
                    if (rb != null) {
                        System.out.println("Annunci Tot: " + rb.getAnnunciTotali());
                        System.out.println("Venduti: " + rb.getAnnunciVenduti());
                    } else System.out.println("Nessun dato.");
                } catch (DAOException e) { System.out.println("Errore: " + e.getMessage()); }
                break;
            case "9": adminController.logout(); break;
            case "0": System.exit(0);
        }
    }
}
