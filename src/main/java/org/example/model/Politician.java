package org.example.model;

import java.io.Serializable;

public class Politician implements Serializable {

    private String id;
    private String name;
    private String dateOfBirth;
    private String currentParty;
    private String homeCounty;
    private String imageUrl; // Added to satisfy the task requirement

    public Politician() {
    }

    public Politician(String id, String name, String dateOfBirth,
                      String currentParty, String homeCounty, String imageUrl) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.currentParty = currentParty;
        this.homeCounty = homeCounty;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Party: " + currentParty +
                " | County: " + homeCounty + " | Image: " + imageUrl;
    }

    // --- Getters and Setters (Required for XML and GUI) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCurrentParty() { return currentParty; }
    public void setCurrentParty(String currentParty) { this.currentParty = currentParty; }

    public String getHomeCounty() { return homeCounty; }
    public void setHomeCounty(String homeCounty) { this.homeCounty = homeCounty; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}