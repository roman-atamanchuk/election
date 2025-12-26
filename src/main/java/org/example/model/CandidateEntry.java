package org.example.model;

import java.io.Serializable;

// Implementing Serializable is required for the Persistence
public class CandidateEntry implements Serializable {

    private String politicianId;
    private String electionId;
    private String partyAtElection; // Handles historical party membership as required
    private int votes;

    public CandidateEntry() {
    }

    public CandidateEntry(String politicianId, String electionId, String partyAtElection, int votes) {
        this.politicianId = politicianId;
        this.electionId = electionId;
        // Logic check: Default to Independent if no party is provided
        this.partyAtElection = (partyAtElection == null || partyAtElection.isBlank()) ? "Independent" : partyAtElection;
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Candidate ID: " + politicianId +
                " | Party: " + partyAtElection +
                " | Votes: " + votes;
    }

    // --- Getters and Setters (Required for XML Persistence) ---
    public String getPoliticianId() { return politicianId; }
    public void setPoliticianId(String politicianId) { this.politicianId = politicianId; }

    public String getElectionId() { return electionId; }
    public void setElectionId(String electionId) { this.electionId = electionId; }

    public String getPartyAtElection() { return partyAtElection; }
    public void setPartyAtElection(String partyAtElection) { this.partyAtElection = partyAtElection; }

    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
}