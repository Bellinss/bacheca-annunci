module it.uniroma2.dicii.ispw.bachecaannunci {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;

    // Esporta il package principale (dove c'è Main.java) verso javafx.graphics
    exports it.uniroma2.dicii.ispw.bachecaannunci to javafx.graphics;

    // permettere al launcher (javafx.graphics) di istanziare la classe Application tramite reflection
    exports it.uniroma2.dicii.ispw.bachecaannunci.view to javafx.graphics;

    // rendere accessibili i package usati da FXMLLoader (injection/reflection)
    opens it.uniroma2.dicii.ispw.bachecaannunci.view to javafx.fxml;
    opens it.uniroma2.dicii.ispw.bachecaannunci.controller to javafx.fxml;
    opens it.uniroma2.dicii.ispw.bachecaannunci to javafx.fxml;
}