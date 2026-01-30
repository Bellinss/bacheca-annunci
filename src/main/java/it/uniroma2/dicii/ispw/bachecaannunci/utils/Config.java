package it.uniroma2.dicii.ispw.bachecaannunci.utils;

public class Config {
    // Definiamo i 3 tipi di persistenza supportati
    public enum PersistenceMode {
        IN_MEMORY,      // Demo: Dati volatili (RAM)
        FILE_SYSTEM,    // File: Dati salvati su disco (.ser)
        MYSQL           // DB: Database relazionale
    }

    // Default: IN_MEMORY
    public static PersistenceMode mode = PersistenceMode.IN_MEMORY;

    // Cartella dove verranno salvati i file .ser (Solo per FILE_SYSTEM)
    public static final String FILE_PATH = "file_data/";
}