package com.example.sneakify.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Class Item viene implementata l'interfaccia Serializable in modo tale da poter essere passata come oggetto alle diverse activity

public class User implements Serializable {

    public String email;
    public String id;
    public User() { } // costruttore vuoto per essere utilizzato da firebase nel pull dei dati da firebase

    public User(String email,String id) {
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
