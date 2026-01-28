package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.HomeAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.SellAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;

import java.util.List;
import java.util.Scanner;

public class CLISellView {
    private final Scanner scanner;
    private final SellAppController sellController = new SellAppController();
    private final HomeAppController homeController = new HomeAppController(); // Per le categorie

    public CLISellView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("\n--- NUOVO ANNUNCIO ---");
        try {
            System.out.print("Titolo: ");
            String titolo = scanner.nextLine();
            System.out.print("Descrizione: ");
            String desc = scanner.nextLine();

            System.out.print("Prezzo: ");
            double prezzo = Double.parseDouble(scanner.nextLine().replace(",", "."));

            List<String> cats = homeController.getCategories();
            System.out.println("Categorie: " + cats);
            System.out.print("Scegli Categoria: ");
            String cat = scanner.nextLine();

            AnnuncioBean bean = new AnnuncioBean(0, titolo, prezzo, desc,
                    Session.getInstance().getLoggedUser().getUsername(), cat);

            sellController.publishAd(bean);
            System.out.println("Annuncio pubblicato con successo!");

        } catch (NumberFormatException e) {
            System.out.println("Errore: Prezzo non valido.");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}