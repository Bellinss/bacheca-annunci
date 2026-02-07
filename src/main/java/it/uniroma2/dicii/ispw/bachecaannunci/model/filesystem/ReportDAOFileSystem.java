package it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem;

import it.uniroma2.dicii.ispw.bachecaannunci.exception.DAOException;
import it.uniroma2.dicii.ispw.bachecaannunci.model.dao.ReportDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.AnnuncioBean;
import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.ReportBean;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAOFileSystem implements ReportDAO {

    private static final String ADS_FILE = Config.FILE_PATH + "annunci.ser";

    public ReportDAOFileSystem() {}

    // Helper per caricare gli annunci
    @SuppressWarnings("unchecked")
    private List<AnnuncioBean> loadAds() {
        File file = new File(ADS_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<AnnuncioBean>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ReportBean generateReport(String targetUsername) throws DAOException {
        // 1. Carica tutti gli annunci attivi
        List<AnnuncioBean> allAds = loadAds();

        // 2. Calcola le statistiche
        int annunciTotali = 0;

        for (AnnuncioBean ad : allAds) {
            // Conta solo gli annunci del venditore target
            if (ad.getVenditore().equals(targetUsername)) {
                annunciTotali++;
            }
        }

        // NOTA: Nella Demo Version (File System), quando un annuncio viene venduto
        // viene rimosso dal file (vedi AdDAOFileSystem).
        // Pertanto non abbiamo uno storico dei venduti.
        int annunciVenduti = 0;
        float percentuale = 0.0f;

        // Simulazione calcolo percentuale (evita divisione per zero)
        if (annunciTotali > 0) {
            percentuale = (float) annunciVenduti / annunciTotali;
        }

        // 3. Data corrente
        Date dataReport = Date.valueOf(LocalDate.now());

        // 4. Crea e restituisci il Bean
        return new ReportBean(
                targetUsername,
                dataReport,
                percentuale,
                annunciTotali,
                annunciVenduti
        );
    }
}