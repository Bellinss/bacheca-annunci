package it.uniroma2.dicii.ispw.bachecaannunci.view;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.LoginAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.RegistrationAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.UserBean;

import java.sql.Date;
import java.sql.SQLException;
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
    private static final String PHONE_REGEX = "^\\d{3}-\\d{3}-\\d{4}$"; // Formato: 333-123-4567
    private static final String ADDRESS_REGEX = "^(Via|Piazza)\\s+[A-Za-z\\s']+\\s+\\d+$";

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

        return switch (choice) {
            case "1" -> {
                performLogin();
                yield false;
            }
            case "2" -> {
                performRegistration();
                yield false;
            }
            case "0" -> true;
            default -> {
                System.out.println("Scelta non valida.");
                yield false;
            }
        };
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

        // Input base (Obbligatori per logica DB, qui uso helper per non lasciarli vuoti)
        String user = getMandatoryString("Username");
        String pass = getMandatoryString("Password");
        String nome = getMandatoryString("Nome");
        String cognome = getMandatoryString("Cognome");

        // 1. VALIDAZIONE DATA
        Date dataNascita = getValidDate();

        // 2. RESIDENZA (OBBLIGATORIA + FORMATO VALIDATO)
        String residenza = getValidAddress("Indirizzo di Residenza", true);

        // 3. FATTURAZIONE (OPZIONALE + FORMATO VALIDATO SE INSERITO)
        String fatturazione = getValidAddress("Indirizzo di Fatturazione", false);

        // 4. SCELTA E VALIDAZIONE RECAPITO
        String tipoRecapito;
        String recapito;

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
                System.out.println("Scelta non valida, riprova.");
            }
        }

        // Creazione Bean
        UserBean newUser = new UserBean(user, pass, nome, cognome, dataNascita,
                residenza, fatturazione, tipoRecapito, recapito);

        try {
            if (regController.registerUser(newUser)) {
                System.out.println("Registrazione completata con successo! Ora puoi effettuare il login.");
            } else {
                System.out.println("Errore: Username già utilizzato.");
            }
        } catch (DAOException e) {
            System.out.println("Errore durante la registrazione: " + e.getMessage());
        }
    }

    // =================================================================================
    // METODI HELPER PER LA VALIDAZIONE
    // =================================================================================

    // Helper per stringhe obbligatorie (non accetta vuoto)
    private String getMandatoryString(String fieldName) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
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

                if (birthDate.isAfter(today)) {
                    System.out.println("ERRORE: La data di nascita non può essere nel futuro.");
                    continue;
                }

                int age = Period.between(birthDate, today).getYears();
                if (age < 18) {
                    System.out.println("ERRORE: Devi essere maggiorenne per registrarti (Età attuale: " + age + ").");
                    continue;
                }
                return Date.valueOf(birthDate);

            } catch (DateTimeParseException e) {
                System.out.println("ERRORE: Formato data non valido. Usa il formato yyyy-mm-dd (es. 1990-12-31).");
            }
        }
    }

    private String getValidAddress(String fieldName, boolean isMandatory) {
        while (true) {
            String suffix = isMandatory ? "" : " (opzionale, premi invio per saltare)";
            System.out.print(fieldName + suffix + ": ");
            String input = scanner.nextLine().trim();

            // Se opzionale e vuoto -> OK
            if (!isMandatory && input.isEmpty()) {
                return "Non specificata";
            }

            // Se obbligatorio e vuoto -> Errore
            if (isMandatory && input.isEmpty()) {
                System.out.println("ERRORE: Il campo '" + fieldName + "' è obbligatorio.");
                continue;
            }

            // Validazione Formato
            if (Pattern.matches(ADDRESS_REGEX, input)) {
                return input;
            } else {
                System.out.println("ERRORE: Formato non valido.");
                System.out.println("Deve iniziare con 'Via' o 'Piazza', seguito dal nome e dal civico finale.");
                System.out.println("Esempi: 'Via Roma 10', 'Piazza di Spagna 50'.");
            }
        }
    }

    private String getValidEmail() {
        while (true) {
            System.out.print("Inserisci Email (es. example@gmail.com): ");
            String input = scanner.nextLine();
            if (Pattern.matches(EMAIL_REGEX, input)) return input;
            System.out.println("ERRORE: Formato email non valido. Riprova.");
        }
    }

    private String getValidPhone() {
        while (true) {
            System.out.print("Inserisci Cellulare (formato 333-123-4567): ");
            String input = scanner.nextLine();
            if (Pattern.matches(PHONE_REGEX, input)) return input;
            System.out.println("ERRORE: Formato cellulare non valido. Deve essere xxx-xxx-xxxx.");
        }
    }
}