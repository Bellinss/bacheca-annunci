package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.LoginAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.RegistrationAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.ValidationException; // IMPORTA
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CLIAccountView {
    private final Scanner scanner;
    private final LoginAppController loginController = new LoginAppController();
    private final RegistrationAppController regController = new RegistrationAppController();

    // Regex per validazione
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$";
    private static final String ADDRESS_REGEX = "^(?i)(via|piazza)\\s+[A-Za-z\\s']+\\s+\\d+$";
    private static final String NAME_REGEX = "^[a-zA-Z\\s']+$";

    public CLIAccountView(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean runGuestMenu() {
        // ... (Codice menu invariato) ...
        System.out.println("\n--- BENVENUTO ---");
        System.out.println("1. Login");
        System.out.println("2. Registrati");
        System.out.println("0. Esci");
        System.out.print("> ");
        String choice = scanner.nextLine();

        return switch (choice) {
            case "1" -> { performLogin(); yield false; }
            case "2" -> { performRegistration(); yield false; }
            case "0" -> true;
            default -> { System.out.println("Scelta non valida."); yield false; }
        };
    }

    private void performLogin() {
        // ... (Codice login invariato) ...
        System.out.print("Username: ");
        String user = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        try {
            Credentials cred = loginController.login(user, pass);
            if (cred != null) {
                Session.getInstance().setLoggedUser(cred);
                System.out.println("Login effettuato con successo!");
            } else {
                System.out.println("Credenziali errate, riprova.");
            }
        } catch (DAOException e) {
            System.out.println("ERRORE LOGIN: " + e.getMessage());
        }
    }

    private void performRegistration() {
        System.out.println("\n--- REGISTRAZIONE ---");

        String user = getMandatoryString("Username");
        String pass = getMandatoryString("Password");
        String nome = getValidName("Nome");
        String cognome = getValidName("Cognome");
        Date dataNascita = getValidDate();
        String residenza = getValidAddress("Indirizzo di Residenza", true);
        String fatturazione = getValidAddress("Indirizzo di Fatturazione", false);

        String tipoRecapito;
        String recapito;
        // ... (Logica scelta recapito invariata) ...
        while (true) {
            System.out.println("Scegli il tipo di recapito:");
            System.out.println("1. Email");
            System.out.println("2. Cellulare");
            System.out.print("> ");
            String scelta = scanner.nextLine();
            if (scelta.equals("1")) {
                tipoRecapito = "Email";
                recapito = getValidEmail();
                break;
            } else if (scelta.equals("2")) {
                tipoRecapito = "Cellulare";
                recapito = getValidPhone();
                break;
            } else {
                System.out.println("Scelta non valida.");
            }
        }

        UserBean newUser = new UserBean(user, pass, nome, cognome, dataNascita,
                residenza, fatturazione, tipoRecapito, recapito);

        try {
            // CHIAMATA CON TRY-CATCH PER VALIDATION EXCEPTION
            if (regController.registerUser(newUser)) {
                System.out.println("Registrazione completata con successo! Ora puoi effettuare il login.");
            } else {
                System.out.println("Errore: Username già utilizzato.");
            }

        } catch (ValidationException e) {
            // GESTIONE 1: Dati non validi (es. password < 4 caratteri)
            System.out.println("\n>>> DATI NON VALIDI: " + e.getMessage());
            System.out.println(">>> Riprova la registrazione prestando attenzione ai requisiti.");

        } catch (DAOException e) {
            // GESTIONE 2: Errore Tecnico
            System.out.println("\n>>> ERRORE SISTEMA: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isBlank()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getValidName(String fieldName) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("ERRORE: Il campo '" + fieldName + "' è obbligatorio.");
                continue;
            }
            if (Pattern.matches(NAME_REGEX, input)) return capitalize(input);
            else System.out.println("ERRORE: " + fieldName + " non valido (inserire solo lettere).");
        }
    }

    private String getMandatoryString(String fieldName) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("ERRORE: Il campo '" + fieldName + "' è obbligatorio.");
        }
    }

    private Date getValidDate() {
        while (true) {
            System.out.print("Data Nascita (yyyy-mm-dd): ");
            String input = scanner.nextLine();
            try {
                LocalDate birthDate = LocalDate.parse(input);
                LocalDate today = LocalDate.now();
                if (birthDate.isAfter(today)) { System.out.println("ERRORE: La data di nascita non può essere nel futuro."); continue; }
                int age = Period.between(birthDate, today).getYears();
                if (age < 18) { System.out.println("ERRORE: Devi essere maggiorenne (Età: " + age + ")."); continue; }
                return Date.valueOf(birthDate);
            } catch (DateTimeParseException e) { System.out.println("ERRORE: Formato data non valido. Usa yyyy-mm-dd."); }
        }
    }

    private String getValidAddress(String fieldName, boolean isMandatory) {
        while (true) {
            String suffix = isMandatory ? "" : " (opzionale, premi invio per saltare)";
            System.out.print(fieldName + suffix + ": ");
            String input = scanner.nextLine().trim();
            if (!isMandatory && input.isEmpty()) return "Non specificata";
            if (isMandatory && input.isEmpty()) { System.out.println("ERRORE: Il campo '" + fieldName + "' è obbligatorio."); continue; }
            if (Pattern.matches(ADDRESS_REGEX, input)) return capitalize(input);
            else System.out.println("ERRORE: Formato non valido. (Es. 'Via Roma 10')");
        }
    }

    private String getValidEmail() {
        while (true) {
            System.out.print("Inserisci Email: ");
            String input = scanner.nextLine();
            if (Pattern.matches(EMAIL_REGEX, input)) return input;
            System.out.println("ERRORE: Formato email non valido.");
        }
    }

    private String getValidPhone() {
        while (true) {
            System.out.print("Inserisci Cellulare (333-123-4567): ");
            String input = scanner.nextLine();
            if (Pattern.matches(PHONE_REGEX, input)) return input;
            System.out.println("ERRORE: Formato cellulare non valido.");
        }
    }
}