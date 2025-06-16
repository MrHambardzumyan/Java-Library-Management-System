package com.library.model;

public class User {
    private final int    id;
    private final String username;
    private final boolean isAdmin;

    public User(int id, String username, boolean isAdmin) {
        this.id       = id;
        this.username = username;
        this.isAdmin  = isAdmin;
    }
    //get setters
    public int    getId()       { return id; }
    public String getUsername() { return username; }
    public boolean isAdmin()    { return isAdmin; }
}
