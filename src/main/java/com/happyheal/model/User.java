package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a user in the system with authentication credentials.
 */
public class User {
    
    @JsonProperty("Username")
    private String username;
    
    @JsonProperty("Password")
    private String password;
    
    @JsonProperty("DisplayName")
    private String displayName;
    
    // Default constructor for Jackson
    public User() {}
    
    public User(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}