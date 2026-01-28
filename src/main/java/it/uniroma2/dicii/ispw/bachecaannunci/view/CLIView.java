package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.*;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.*;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CLIView {

    private final Scanner scanner;

    // Controllers
    private final LoginAppController loginController;
    private final RegistrationAppController regController;
    private final HomeAppController homeController;
    private final SellAppController sellController;
    private final InboxAppController inboxController;
    private final ChatAppController chatController;
    private final AdPageAppController adPageController;
    private final NoteAppController noteController;
    private final CommentAppController commentController;
    private final AdminAppController adminController;

    public CLIView() {
        this.scanner = new Scanner(System.in);
        // Inizializzazione di TUTTI i controller applicativi
        this.loginController = new LoginAppController();
        this.regController = new RegistrationAppController();
        this.homeController = new HomeAppController();
        this.sellController = new SellAppController();
        this.inboxController = new InboxAppController();
        this.chatController = new ChatAppController();
        this.adPageController = new AdPageAppController();
        this.noteController = new NoteAppController();
        this.commentController = new CommentAppController();
        this.adminController = new AdminAppController();
    }

    public void run() {
        System.out.println("=========================================");
        System.out.println("   BACHECA ANNUNCI - CLI MODE (" + (Config.IS_DEMO_VERSION ? "DEMO" : "FULL") + ")");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            Credentials user = Session.getInstance().getLoggedUser();

            if (user == null) {
                running = showGuestMenu();
            } else if (user.getRole() == Role.AMMINISTRATORE) {
                running = showAdminMenu();
            } else {
                running = showUserMenu();
            }
        }
        System.out.println("Chiusura applicazione...");
        System.exit(0);
    }

    // =================================================================================
    // MENU PRINCIPALI
    // =================================================================================

    private boolean showGuestMenu() {
        System.out.println("\n--- MENU OSPITE ---");
        System.out.println("1. Login");
        System.out.println("2. Registrati");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        return switch (choice) {
            case "1" -> {
                performLogin();
                yield true;
            }
            case "2" -> {
                performRegistration();
                yield true;
            }
            case "0" -> false;
            default -> {
                System.out.println("Scelta non valida.");
                yield true;
            }
        };
    }

    private boolean showUserMenu() {
        String username = Session.getInstance().getLoggedUser().getUsername();
        System.out.println("\n--- MENU UTENTE: " + username + " ---");
        System.out.println("1. Bacheca Annunci (Cerca/Visualizza)");
        System.out.println("2. Pubblica Annuncio");
        System.out.println("3. Messaggi (Inbox)");
        System.out.println("4. Notifiche");
        System.out.println("5. Annunci Seguiti (Preferiti)");
        System.out.println("9. Logout");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": browseAds(false); break; // false = tutti
            case "2": publishAd(); break;
            case "3": showInbox(); break;
            case "4": showNotifications(); break;
            case "5": browseAds(true); break; // true = solo seguiti
            case "9": homeController.logout(); break;
            case "0": return false;
            default: System.out.println("Comando non valido.");
        }
        return true;
    }

    private boolean showAdminMenu() {
        String username = Session.getInstance().getLoggedUser().getUsername();
        System.out.println("\n--- MENU AMMINISTRATORE: " + username + " ---");
        System.out.println("1. Aggiungi Categoria");
        System.out.println("2. Genera Report Utente");
        System.out.println("9. Logout");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": adminAddCategory(); break;
            case "2": adminGenerateReport(); break;
            case "9": adminController.logout(); break;
            case "0": return false;
            default: System.out.println("Comando non valido.");
        }
        return true;
    }

    // =================================================================================
    // FUNZIONI DI AUTENTICAZIONE
    // =================================================================================

    private void performLogin() {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        try {
            Credentials cred = loginController.login(user, pass);
            if (cred != null) {
                Session.getInstance().setLoggedUser(cred);
                System.out.println("Login effettuato come " + cred.getRole());
            }
        } catch (DAOException | SQLException e) {
            System.out.println("ERRORE LOGIN: " + e.getMessage());
        }
    }

    private void performRegistration() {
        System.out.println("\n--- REGISTRAZIONE ---");
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();

        System.out.print("Data Nascita (yyyy-mm-dd): ");
        Date dataNascita;
        try {
            dataNascita = Date.valueOf(scanner.nextLine());
        } catch (Exception e) {
            dataNascita = new Date(System.currentTimeMillis());
            System.out.println("Formato data errato, uso data odierna.");
        }
        System.out.print("Email: ");
        String email = scanner.nextLine();

        UserBean newUser = new UserBean(user, pass, nome, cognome, dataNascita,
                "Non specificata", "Non specificata", "Email", email);
        try {
            if (regController.registerUser(newUser)) {
                System.out.println("Registrazione OK! Effettua il login.");
            } else {
                System.out.println("Username già esistente.");
            }
        } catch (DAOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // =================================================================================
    // GESTIONE ANNUNCI (HOME & DETTAGLI)
    // =================================================================================

    private void browseAds(boolean onlyFollowed) {
        try {
            System.out.println("\n--- " + (onlyFollowed ? "PREFERITI" : "LISTA ANNUNCI") + " ---");
            // Opzione filtri
            if (!onlyFollowed) {
                System.out.print("Vuoi filtrare? (s/N): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    System.out.print("Categoria (o invio per tutte): ");
                    String cat = scanner.nextLine();
                    System.out.print("Testo ricerca (o invio per vuoto): ");
                    String txt = scanner.nextLine();
                    if (cat.isEmpty()) cat = "Tutte le categorie";

                    List<AnnuncioBean> filtered = homeController.filterAds(cat, txt, false);
                    printAdsList(filtered);
                    return;
                }
            }

            // Lista standard
            List<AnnuncioBean> ads = onlyFollowed ?
                    homeController.filterAds("Tutte le categorie", "", true) :
                    homeController.getAllAds();

            printAdsList(ads);

        } catch (DAOException e) {
            System.out.println("Errore recupero annunci: " + e.getMessage());
        }
    }

    private void printAdsList(List<AnnuncioBean> ads) {
        if (ads.isEmpty()) {
            System.out.println("Nessun annuncio trovato.");
            return;
        }
        for (AnnuncioBean ad : ads) {
            System.out.printf("[%d] %s (%.2f €) - %s\n", ad.getId(), ad.getTitolo(), ad.getImporto(), ad.getVenditore());
        }

        System.out.print("\nInserisci ID annuncio per dettagli (o 0 per tornare indietro): ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (id != 0) showAdDetails(id, ads);
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.");
        }
    }

    private void showAdDetails(int adId, List<AnnuncioBean> contextList) {
        // Cerchiamo l'annuncio nella lista caricata (per evitare query inutile se abbiamo già i dati)
        // Ma per avere dati freschi (es. note) è meglio ricaricare i dettagli se necessario.
        // Qui usiamo l'oggetto della lista per semplicità di visualizzazione base.
        AnnuncioBean ad = contextList.stream().filter(a -> a.getId() == adId).findFirst().orElse(null);

        if (ad == null) {
            System.out.println("Annuncio non trovato nella lista corrente.");
            return;
        }

        boolean inDetails = true;
        while (inDetails) {
            System.out.println("\n=================================");
            System.out.println("TITOLO: " + ad.getTitolo());
            System.out.println("PREZZO: " + ad.getImporto() + " €");
            System.out.println("VENDITORE: " + ad.getVenditore());
            System.out.println("CATEGORIA: " + ad.getCategoria());
            System.out.println("DESCRIZIONE: " + ad.getDescrizione());
            System.out.println("=================================");

            boolean isOwner = adPageController.isOwner(ad.getVenditore());

            System.out.println("AZIONI:");
            System.out.println("1. Visualizza Commenti");
            if (isOwner) {
                System.out.println("2. Visualizza/Aggiungi NOTE (Privato)");
                System.out.println("3. Segna come VENDUTO");
            } else {
                System.out.println("2. Contatta Venditore");
                System.out.println("3. Aggiungi Commento");
                try {
                    boolean isFollowing = adPageController.isFollowing(adId);
                    System.out.println("4. " + (isFollowing ? "Smetti di seguire" : "Segui Annuncio"));
                } catch (DAOException e) { System.out.println("4. Follow (Errore check)"); }
            }
            System.out.println("0. Indietro");
            System.out.print("> ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1": showComments(adId); break;
                    case "2":
                        if (isOwner) handleNotes(adId);
                        else contactSeller(ad.getVenditore());
                        break;
                    case "3":
                        if (isOwner) {
                            adPageController.markAsSold(adId);
                            System.out.println("Annuncio venduto! Torno al menu.");
                            inDetails = false;
                        } else {
                            System.out.print("Commento: ");
                            commentController.postComment(scanner.nextLine(), adId);
                            System.out.println("Commento inviato.");
                        }
                        break;
                    case "4":
                        if (!isOwner) {
                            boolean res = adPageController.followAd(adId);
                            System.out.println(res ? "Ora segui l'annuncio!" : "Hai smesso di seguire o errore.");
                        }
                        break;
                    case "0": inDetails = false; break;
                    default: System.out.println("Scelta non valida");
                }
            } catch (DAOException e) {
                System.out.println("Errore operazione: " + e.getMessage());
            }
        }
    }

    private void publishAd() {
        System.out.println("\n--- NUOVO ANNUNCIO ---");
        System.out.print("Titolo: ");
        String titolo = scanner.nextLine();
        System.out.print("Descrizione: ");
        String desc = scanner.nextLine();
        System.out.print("Prezzo: ");
        double prezzo = Double.parseDouble(scanner.nextLine().replace(",", "."));

        try {
            List<String> cats = homeController.getCategories();
            System.out.println("Categorie: " + cats);
            System.out.print("Scegli Categoria: ");
            String cat = scanner.nextLine();

            AnnuncioBean bean = new AnnuncioBean(0, titolo, prezzo, desc,
                    Session.getInstance().getLoggedUser().getUsername(), cat);
            sellController.publishAd(bean);
            System.out.println("Pubblicato!");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // =================================================================================
    // MESSAGGI E CHAT
    // =================================================================================

    private void showInbox() {
        try {
            List<String> conversations = inboxController.getActiveConversations();
            if (conversations.isEmpty()) {
                System.out.println("Nessuna conversazione attiva.");
                return;
            }
            System.out.println("\n--- CONVERSAZIONI ATTIVE ---");
            for (int i = 0; i < conversations.size(); i++) {
                System.out.println((i + 1) + ". " + conversations.get(i));
            }
            System.out.print("Inserisci nome utente per aprire chat (o 0 per uscire): ");
            String target = scanner.nextLine();
            if (!target.equals("0")) {
                showChat(target);
            }
        } catch (DAOException e) {
            System.out.println("Errore Inbox: " + e.getMessage());
        }
    }

    private void showChat(String otherUser) {
        boolean chatting = true;
        System.out.println("\n--- CHAT CON " + otherUser.toUpperCase() + " ---");
        while (chatting) {
            try {
                // Recupera messaggi
                List<MessageBean> msgs = chatController.getMessages(otherUser);
                if (msgs.isEmpty()) System.out.println("(Nessun messaggio)");
                for (MessageBean m : msgs) {
                    System.out.println(m.getMittente() + ": " + m.getTesto());
                }

                System.out.println("\n[S] Scrivi messaggio | [R] Ricarica | [E] Esci");
                String cmd = scanner.nextLine().toUpperCase();
                if (cmd.equals("E")) chatting = false;
                else if (cmd.equals("S")) {
                    System.out.print("Messaggio: ");
                    String txt = scanner.nextLine();
                    chatController.sendMessage(otherUser, txt);
                }
                // Se R, il loop ricomincia e ricarica i messaggi
            } catch (DAOException e) {
                System.out.println("Errore Chat: " + e.getMessage());
                chatting = false;
            }
        }
    }

    private void contactSeller(String seller) {
        System.out.println("Apro la chat con il venditore...");
        showChat(seller);
    }

    // =================================================================================
    // NOTE E COMMENTI
    // =================================================================================

    private void showComments(int adId) throws DAOException {
        List<CommentBean> comments = commentController.getComments(adId);
        System.out.println("\n--- COMMENTI ---");
        if (comments.isEmpty()) System.out.println("Nessun commento.");
        for (CommentBean c : comments) {
            System.out.println("- " + c.getTesto());
        }
        System.out.println("(Premi invio per continuare)");
        scanner.nextLine();
    }

    private void handleNotes(int adId) throws DAOException {
        System.out.println("\n--- LE TUE NOTE ---");
        List<NoteBean> notes = noteController.getNotes(adId);
        for (NoteBean n : notes) System.out.println("* " + n.getTesto());

        System.out.print("Vuoi aggiungere una nota? (s/N): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Testo nota: ");
            noteController.addNote(scanner.nextLine(), adId);
            System.out.println("Nota aggiunta!");
        }
    }

    // =================================================================================
    // NOTIFICHE
    // =================================================================================

    private void showNotifications() {
        try {
            List<NotificationBean> list = homeController.getNotifications();
            System.out.println("\n--- NOTIFICHE ---");
            if (list.isEmpty()) System.out.println("Nessuna notifica.");
            for (NotificationBean n : list) System.out.println(n);

            if (!list.isEmpty()) {
                System.out.print("Vuoi cancellarle tutte? (s/N): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    homeController.clearAllNotifications();
                    System.out.println("Cancellate.");
                }
            }
        } catch (DAOException e) {
            System.out.println("Errore notifiche: " + e.getMessage());
        }
    }

    // =================================================================================
    // FUNZIONI ADMIN
    // =================================================================================

    private void adminAddCategory() {
        System.out.print("Nome nuova categoria: ");
        String nome = scanner.nextLine();
        // Path fittizio per demo
        try {
            adminController.addCategory("path/to/icon", nome);
            System.out.println("Categoria aggiunta.");
        } catch (DAOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void adminGenerateReport() {
        System.out.print("Username utente target: ");
        String target = scanner.nextLine();
        try {
            ReportBean rb = adminController.generateUserReport(target);
            if (rb != null) {
                System.out.println("\n--- REPORT: " + target + " ---");
                System.out.println("Annunci Totali: " + rb.getAnnunciTotali());
                System.out.println("Annunci Venduti: " + rb.getAnnunciVenduti());
                System.out.println("Percentuale Successo: " + (rb.getPercentuale() * 100) + "%");
            } else {
                System.out.println("Nessun dato disponibile.");
            }
        } catch (DAOException e) {
            System.out.println("Errore report: " + e.getMessage());
        }
    }
}