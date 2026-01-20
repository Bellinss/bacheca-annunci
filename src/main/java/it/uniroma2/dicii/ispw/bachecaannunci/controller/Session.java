package it.uniroma2.dicii.ispw.bachecaannunci.controller;

import it.uniroma2.dicii.ispw.bachecaannunci.model.domain.Credentials;

public class Session {
    private static Session instance = null;
    private Credentials loggedUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setLoggedUser(Credentials user) {
        this.loggedUser = user;
    }

    public Credentials getLoggedUser() {
        return loggedUser;
    }
}