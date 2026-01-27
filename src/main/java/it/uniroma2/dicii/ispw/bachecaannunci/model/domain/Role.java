package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.io.Serializable;

public enum Role implements Serializable {
    AMMINISTRATORE(1),
    UTENTE(2);

    private static final long serialVersionUID = 1L;
    private final int id;

    private Role(int id) {
        this.id = id;
    }

    public static Role fromInt(int id) {
        for (Role type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
