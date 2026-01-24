package it.uniroma2.dicii.ispw.bachecaannunci.appcontroller;

public class ItemAppController {

    // Logica di business per la formattazione del prezzo
    public String formatPrice(double price) {
        return String.format("€ %.2f", price);
    }
}