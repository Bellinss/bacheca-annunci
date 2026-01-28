package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.HomeAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NotificationBean;

import java.util.List;
import java.util.Scanner;

public class CLIHomeView {
    private final Scanner scanner;
    private final HomeAppController homeController = new HomeAppController();

    // View Funzionali
    private final CLISellView sellView;
    private final CLIMessageView messageView;
    private final CLIAdDetailsView adDetailsView;

    public CLIHomeView(Scanner scanner) {
        this.scanner = scanner;
        this.sellView = new CLISellView(scanner);
        this.messageView = new CLIMessageView(scanner);
        this.adDetailsView = new CLIAdDetailsView(scanner);
    }

    public void run() {
        String username = Session.getInstance().getLoggedUser().getUsername();
        System.out.println("\n--- MENU UTENTE: " + username + " ---");
        System.out.println("1. Cerca/Visualizza Annunci");
        System.out.println("2. Pubblica Annuncio");
        System.out.println("3. Messaggi (Inbox)");
        System.out.println("4. Notifiche");
        System.out.println("5. Annunci Preferiti");
        System.out.println("9. Logout");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": browseAds(false); break;
            case "2": sellView.run(); break;
            case "3": messageView.run(); break;
            case "4": showNotifications(); break;
            case "5": browseAds(true); break;
            case "9": homeController.logout(); break;
            case "0": System.exit(0);
            default: System.out.println("Comando non valido.");
        }
    }

    private void browseAds(boolean onlyFollowed) {
        try {
            System.out.println("\n--- " + (onlyFollowed ? "PREFERITI" : "LISTA ANNUNCI") + " ---");
            List<AnnuncioBean> ads;

            if (!onlyFollowed) {
                System.out.print("Vuoi filtrare? (s/N): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    System.out.print("Categoria (invio per tutte): ");
                    String cat = scanner.nextLine();
                    if (cat.isEmpty()) cat = "Tutte le categorie";
                    System.out.print("Testo (invio per vuoto): ");
                    String txt = scanner.nextLine();
                    ads = homeController.filterAds(cat, txt, false);
                } else {
                    ads = homeController.getAllAds();
                }
            } else {
                ads = homeController.filterAds("Tutte le categorie", "", true);
            }

            if (ads.isEmpty()) {
                System.out.println("Nessun annuncio trovato.");
                return;
            }

            for (AnnuncioBean ad : ads) {
                System.out.printf("[%d] %s (%.2f €) - %s\n", ad.getId(), ad.getTitolo(), ad.getImporto(), ad.getVenditore());
            }

            System.out.print("\nInserisci ID annuncio per dettagli (o 0 per menu): ");
            try {
                int id = Integer.parseInt(scanner.nextLine());
                if (id != 0) {
                    // Trova l'oggetto annuncio e apri i dettagli
                    AnnuncioBean selected = ads.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
                    if (selected != null) adDetailsView.run(selected);
                    else System.out.println("ID non presente in questa lista.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ID non valido.");
            }

        } catch (DAOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void showNotifications() {
        try {
            List<NotificationBean> list = homeController.getNotifications();
            System.out.println("\n--- NOTIFICHE ---");
            if (list.isEmpty()) System.out.println("Nessuna notifica.");
            for (NotificationBean n : list) System.out.println(n);

            if (!list.isEmpty()) {
                System.out.print("Cancellare tutto? (s/N): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    homeController.clearAllNotifications();
                    System.out.println("Fatto.");
                }
            }
        } catch (DAOException e) {
            System.out.println("Errore notifiche: " + e.getMessage());
        }
    }
}