package org.example.model;

import org.example.util.SimpleList;
import java.io.Serializable;

public class Election implements Serializable {

    private String id;
    private ElectionType type;
    private int year;
    private String location;   // e.g. county for locals
    private int seats;         // Number of winners/seats

    // This custom list holds the CandidateEntry objects for THIS election
    private SimpleList<CandidateEntry> candidateEntries;

    // 1. REQUIRED for XML Persistence (No-argument constructor)
    public Election() {
        this.candidateEntries = new SimpleList<>();
    }

    public Election(String id, ElectionType type, int year, String location, int seats) {
        this.id = id;
        this.type = type;
        this.year = year;
        this.location = location;
        this.seats = seats;
        this.candidateEntries = new SimpleList<>();
    }

    // Link a politician to this specific election
    // Inside Election.java

    public void addCandidateEntry(CandidateEntry entry) {
        // Automatically force the entry to have THIS election's ID
        entry.setElectionId(this.id);

        // Add it to our internal list
        this.candidateEntries.add(entry);
    }

    // 2. Getters and Setters for XML Persistence
    public SimpleList<CandidateEntry> getCandidateEntries() {
        return candidateEntries;
    }

    public void setCandidateEntries(SimpleList<CandidateEntry> candidateEntries) {
        this.candidateEntries = candidateEntries;
    }

    @Override
    public String toString() {
        return id + " (" + year + ", " + type + ")";
    }

    // Standard Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ElectionType getType() { return type; }
    public void setType(ElectionType type) { this.type = type; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
}