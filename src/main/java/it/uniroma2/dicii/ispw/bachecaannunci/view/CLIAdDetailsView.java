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
            printAdHeader(ad);

            boolean isOwner = adPageController.isOwner(ad.getVenditore());
            boolean isFollowing = checkIsFollowing(isOwner, ad.getId());

            printMenu(isOwner, isFollowing);

            String choice = scanner.nextLine();
            try {
                // La logica complessa dello switch è stata spostata in handleChoice
                inDetails = handleChoice(choice, ad, isOwner, isFollowing);
            } catch (DAOException e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }
    }

    // --- METODI HELPER ---

    private void printAdHeader(AnnuncioBean ad) {
        System.out.println("\n===== DETTAGLIO ANNUNCIO =====");
        System.out.println("TITOLO: " + ad.getTitolo());
        System.out.println("PREZZO: " + ad.getImporto() + " €");
        System.out.println("VENDITORE: " + ad.getVenditore());
        System.out.println("DESCRIZIONE: " + ad.getDescrizione());
        System.out.println("================================");
    }

    private boolean checkIsFollowing(boolean isOwner, int adId) {
        if (isOwner) return false;
        try {
            return adPageController.isFollowing(adId);
        } catch (DAOException e) {
            return false;
        }
    }

    private void printMenu(boolean isOwner, boolean isFollowing) {
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
    }

    private boolean handleChoice(String choice, AnnuncioBean ad, boolean isOwner, boolean isFollowing) throws DAOException {
        return switch (choice) {
            case "1" -> {
                showComments(ad.getId());
                yield true;
            }
            case "2" -> {
                handleOption2(isOwner, ad);
                yield true;
            }
            case "3" -> handleOption3(isOwner, ad);
            case "4" -> {
                handleOption4(isOwner, isFollowing, ad);
                yield true;
            }
            case "0" -> false; // Esce dal ciclo
            default -> {
                System.out.println("Scelta non valida");
                yield true;
            }
        };
    }

    private void handleOption2(boolean isOwner, AnnuncioBean ad) throws DAOException {
        if (isOwner) {
            handleNotes(ad.getId());
        } else {
            messageView.openChat(ad.getVenditore());
        }
    }

    private boolean handleOption3(boolean isOwner, AnnuncioBean ad) throws DAOException {
        if (isOwner) {
            adPageController.markAsSold(ad.getId());
            System.out.println("Oggetto venduto! Ritorno alla home.");
            return false; // Torna alla home
        } else {
            System.out.print("Testo commento: ");
            commentController.postComment(scanner.nextLine(), ad.getId());
            System.out.println("Commento pubblicato.");
            return true;
        }
    }

    private void handleOption4(boolean isOwner, boolean isFollowing, AnnuncioBean ad) throws DAOException {
        if (!isOwner) {
            if (isFollowing) {
                System.out.println("--> Annuncio già presente nei preferiti!");
            } else {
                boolean res = adPageController.followAd(ad.getId());
                if (res) System.out.println("--> Aggiunto ai preferiti!");
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