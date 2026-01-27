package it.uniroma2.dicii.ispw.bachecaannunci.model.domain;

import java.io.Serializable;

public class Credentials implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final Role role;

    public Credentials(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
