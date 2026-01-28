package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.LoginAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.RegistrationAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class CLIAccountView {
    private final Scanner scanner;
    private final LoginAppController loginController = new LoginAppController();
    private final RegistrationAppController regController = new RegistrationAppController();

    public CLIAccountView(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean runGuestMenu() {
        System.out.println("\n--- BENVENUTO ---");
        System.out.println("1. Login");
        System.out.println("2. Registrati");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": performLogin(); return false; // false = non uscire dall'app
            case "2": performRegistration(); return false;
            case "0": return true; // true = esci dall'app
            default: System.out.println("Scelta non valida."); return false;
        }
    }

    private void performLogin() {
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        try {
            Credentials cred = loginController.login(user, pass);
            if (cred != null) {
                Session.getInstance().setLoggedUser(cred);
                System.out.println("Login effettuato con successo!");
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
            System.out.println("Formato errato, uso data odierna.");
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
}