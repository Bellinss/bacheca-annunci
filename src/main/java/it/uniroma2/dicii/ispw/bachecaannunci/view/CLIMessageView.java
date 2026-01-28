package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.ChatAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.InboxAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.MessageBean;

import java.util.List;
import java.util.Scanner;

public class CLIMessageView {
    private final Scanner scanner;
    private final InboxAppController inboxController = new InboxAppController();
    private final ChatAppController chatController = new ChatAppController();

    public CLIMessageView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        try {
            List<String> conversations = inboxController.getActiveConversations();
            if (conversations.isEmpty()) {
                System.out.println("Nessuna conversazione attiva.");
                return;
            }
            System.out.println("\n--- INBOX ---");
            for (String user : conversations) System.out.println("- " + user);

            System.out.print("Scrivi nome utente per aprire chat (o 0 per uscire): ");
            String target = scanner.nextLine();
            if (!target.equals("0")) {
                openChat(target);
            }
        } catch (DAOException e) {
            System.out.println("Errore Inbox: " + e.getMessage());
        }
    }

    public void openChat(String otherUser) {
        boolean chatting = true;
        System.out.println("\n--- CHAT CON " + otherUser.toUpperCase() + " ---");

        while (chatting) {
            try {
                List<MessageBean> msgs = chatController.getMessages(otherUser);
                if (msgs.isEmpty()) System.out.println("(Inizia la conversazione...)");
                for (MessageBean m : msgs) {
                    System.out.println(m.getMittente() + ": " + m.getTesto());
                }

                System.out.println("\n[S] Scrivi | [R] Ricarica | [E] Esci");
                String cmd = scanner.nextLine().toUpperCase();

                if (cmd.equals("E")) chatting = false;
                else if (cmd.equals("S")) {
                    System.out.print("Messaggio: ");
                    String txt = scanner.nextLine();
                    if (!txt.isEmpty()) chatController.sendMessage(otherUser, txt);
                }
            } catch (DAOException e) {
                System.out.println("Errore Chat: " + e.getMessage());
                chatting = false;
            }
        }
    }
}