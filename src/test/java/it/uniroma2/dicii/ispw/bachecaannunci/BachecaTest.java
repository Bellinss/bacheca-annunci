package it.uniroma2.dicii.ispw.bachecaannunci;

import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.LoginAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.NoteAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.appcontroller.SellAppController;
import it.uniroma2.dicii.ispw.bachecaannunci.controller.Session;
import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.DAOFactory;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.NoteBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Role;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di Test per le funzionalità principali del sistema.
 * Configurazione: IN_MEMORY per isolamento e velocità.
 */
class BachecaTest {

    // Controller Applicativi da testare
    private LoginAppController loginController;
    private SellAppController sellController;
    private NoteAppController noteController;

    @BeforeEach
    void setUp() {
        // Usiamo la memoria volatile per i test
        // Così non sporchiamo il DB reale o il File System
        Config.mode = Config.PersistenceMode.IN_MEMORY;

        // Inizializzazione dei controller
        loginController = new LoginAppController();
        sellController = new SellAppController();
        noteController = new NoteAppController();

        // Resettiamo la sessione prima di ogni test
        Session.getInstance().setLoggedUser(null);
    }

    /**
     * Test Case 1: Verifica del Login
     * Tester: [Matteo Bellini]
     * Obiettivo: Verificare che un utente esistente (definito staticamente in UserDAOInMemory)
     * riesca ad accedere e che la sessione venga impostata.
     */
    @Test
    void testLoginSuccess() throws DAOException {
        // Dati presenti staticamente in UserDAOInMemory
        String username = "mario";
        String password = "rossi";

        // Azione
        Credentials cred = loginController.login(username, password);

        // Asserzioni
        assertNotNull(cred, "Le credenziali non dovrebbero essere null per un utente valido");
        assertEquals(username, cred.getUsername(), "Lo username restituito deve corrispondere");
        assertEquals(Role.UTENTE, cred.getRole(), "Il ruolo deve essere UTENTE");

        // Verifica effetto collaterale: Sessione popolata
        assertNotNull(Session.getInstance().getLoggedUser(), "L'utente dovrebbe essere salvato in sessione");
    }

    /**
     * Test Case 2: Pubblicazione Annuncio
     * Tester: [Matteo Bellini]
     * Obiettivo: Verificare che un utente loggato possa pubblicare un annuncio
     * e che questo venga correttamente salvato nel DAO.
     */
    @Test
    void testPublishAd() throws DAOException {
        // 1. Setup: Simuliamo il login (necessario per pubblicare)
        Credentials user = new Credentials("mario", "rossi", Role.UTENTE);
        Session.getInstance().setLoggedUser(user);

        // 2. Setup: Creiamo il bean dell'annuncio
        AnnuncioBean nuovoAnnuncio = new AnnuncioBean(
                0,
                "iPhone Usato",
                500.0,
                "Buone condizioni",
                "mario",
                "Elettronica"
        );

        // 3. Azione: Pubblicazione
        sellController.publishAd(nuovoAnnuncio);

        // 4. Verifica: Recuperiamo tutti gli annunci dal DAO per vedere se c'è
        List<AnnuncioBean> annunci = DAOFactory.getAdDAO().findAll();

        boolean trovato = annunci.stream()
                .anyMatch(a -> a.getTitolo().equals("iPhone Usato") &&
                        a.getVenditore().equals("mario"));

        assertTrue(trovato, "L'annuncio pubblicato dovrebbe essere presente nella lista");
    }

    /**
     * Test Case 3: Inserimento Nota (Senza Notifiche)
     * Tester: [Matteo Bellini]
     * Obiettivo: Verificare l'aggiunta di una nota privata a un annuncio.
     * Verifica implicitamente che la logica delle notifiche rimossa non causi crash.
     */
    @Test
    void testAddNote() throws DAOException {
        // 1. Setup: Utente loggato
        String username = "mario";
        Session.getInstance().setLoggedUser(new Credentials(username, "pass", Role.UTENTE));

        // 2. Setup: Creiamo un annuncio fittizio nel sistema (per avere un ID valido)
        DAOFactory.getAdDAO().createAd("Test Ad", 100, "Desc", username, "Test");
        // Recuperiamo l'ID dell'annuncio appena creato (assumendo sia l'ultimo o cercandolo)
        List<AnnuncioBean> ads = DAOFactory.getAdDAO().findAll();
        int adId = ads.get(ads.size() - 1).getId();

        // 3. Azione: Aggiungi nota
        String notaText = "Nota personale: il cliente sembra interessato";
        noteController.addNote(notaText, adId);

        // 4. Verifica: Recupero note
        List<NoteBean> notes = noteController.getNotes(adId);

        assertFalse(notes.isEmpty(), "La lista delle note non deve essere vuota");
        assertEquals(notaText, notes.get(0).getTesto(), "Il testo della nota deve corrispondere");
    }
}
