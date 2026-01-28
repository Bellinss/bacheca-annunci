package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.*;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.CommentBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;

import java.util.List;
import java.util.Scanner;

public class CLIAdDetailsView {
    private final Scanner scanner;
    private final AdPageAppController adPageController = new AdPageAppController();
    private final CommentAppController commentController = new CommentAppController();
    private final NoteAppController noteController = new NoteAppController();
    private final CLIMessageView messageView;

    public CLIAdDetailsView(Scanner scanner) {
        this.scanner = scanner;
        this.messageView = new CLIMessageView(scanner);
    }

    public void run(AnnuncioBean ad) {
        boolean inDetails = true;
        while (inDetails) {
            System.out.println("\n===== DETTAGLIO ANNUNCIO =====");
            System.out.println("TITOLO: " + ad.getTitolo());
            System.out.println("PREZZO: " + ad.getImporto() + " €");
            System.out.println("VENDITORE: " + ad.getVenditore());
            System.out.println("DESCRIZIONE: " + ad.getDescrizione());
            System.out.println("================================");

            boolean isOwner = adPageController.isOwner(ad.getVenditore());

            // CONTROLLO PREVENTIVO: L'utente segue già questo annuncio?
            boolean isFollowing = false;
            try {
                if (!isOwner) {
                    isFollowing = adPageController.isFollowing(ad.getId());
                }
            } catch (DAOException e) {
                // Se c'è errore, assumiamo false per non bloccare la UI
                isFollowing = false;
            }

            System.out.println("AZIONI:");
            System.out.println("1. Visualizza Commenti");

            if (isOwner) {
                System.out.println("2. Gestione NOTE (Privato)");
                System.out.println("3. Segna come VENDUTO");
            } else {
                System.out.println("2. Contatta Venditore");
                System.out.println("3. Aggiungi Commento");

                if (isFollowing) {
                    System.out.println("4. (Annuncio già presente nei preferiti)");
                } else {
                    System.out.println("4. Segui Annuncio");
                }
            }
            System.out.println("0. Indietro");
            System.out.print("> ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1": showComments(ad.getId()); break;
                    case "2":
                        if (isOwner) handleNotes(ad.getId());
                        else messageView.openChat(ad.getVenditore());
                        break;
                    case "3":
                        if (isOwner) {
                            adPageController.markAsSold(ad.getId());
                            System.out.println("Oggetto venduto! Ritorno alla home.");
                            inDetails = false;
                        } else {
                            System.out.print("Testo commento: ");
                            commentController.postComment(scanner.nextLine(), ad.getId());
                            System.out.println("Commento pubblicato.");
                        }
                        break;
                    case "4":
                        if (!isOwner) {
                            // MODIFICA QUI: Azione condizionale
                            if (isFollowing) {
                                System.out.println("--> Annuncio già presente nei preferiti!");
                            } else {
                                boolean res = adPageController.followAd(ad.getId());
                                if (res) System.out.println("--> Aggiunto ai preferiti!");
                            }
                        }
                        break;
                    case "0": inDetails = false; break;
                    default: System.out.println("Scelta non valida");
                }
            } catch (DAOException e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }
    }

    private void showComments(int adId) throws DAOException {
        List<CommentBean> comments = commentController.getComments(adId);
        System.out.println("\n--- COMMENTI ---");
        if (comments.isEmpty()) System.out.println("Nessun commento.");
        for (CommentBean c : comments) System.out.println("- " + c.getTesto());
        System.out.println("(Premi invio per continuare)");
        scanner.nextLine();
    }

    private void handleNotes(int adId) throws DAOException {
        System.out.println("\n--- NOTE PRIVATE ---");
        List<NoteBean> notes = noteController.getNotes(adId);
        for (NoteBean n : notes) System.out.println("* " + n.getTesto());

        System.out.print("Vuoi aggiungere una nota? (s/N): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Testo: ");
            noteController.addNote(scanner.nextLine(), adId);
            System.out.println("Nota salvata.");
        }
    }
}