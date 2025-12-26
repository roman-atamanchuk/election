package org.example.controller;

import org.example.model.*;
import org.example.util.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

/**
 * ElectionController manages the core logic of the application,
 * including data storage, searching, sorting, and file persistence.
 * Searching Algorithms
 * Method: searchPoliticianByID(String id)
 *Method: searchPoliticianByName(String name)
 * Method: searchElectionByID(String id)
 * Algorithm: Custom Hashing (O(1)) via CustomHashTable
 * Method: searchPoliticianByPartialName(String partName)
 * Algorithm: Linear Search
 * Method: filterPoliticiansByLocation(String county)
 * Algorithm: Linear Search
 * Method: filterPoliticiansByParty(String party)
 * Algorithm: Linear Search (O(n))
 *
 * Sorting Algorithms
 * Method: sortCandidatesByVotes(SimpleList<CandidateEntry> list)
 * Algorithm: Selection Sort (Descending Order)
 * Method: sortPoliticiansByName()
 * Algorithm: Selection Sort (Ascending Order)
 */
public class ElectionController {
    // 1. MASTER LISTS (Used for data persistence, filtering, and sorting)
    private SimpleList<Politician> politicians = new SimpleList<>(); // Stores all politician objects
    private SimpleList<Election> elections = new SimpleList<>();     // Stores all election objects
    private SimpleList<CandidateEntry> allEntries = new SimpleList<>(); // Stores links between politicians and elections

    // 2. HASH TABLES (Used to satisfy the O(1) instant search requirement)
    private CustomHashTable<Politician> polByName = new CustomHashTable<>(50); // Fast lookup by politician name
    private CustomHashTable<Politician> polByID = new CustomHashTable<>(50);   // Fast lookup by politician unique ID
    private CustomHashTable<Election> elByID = new CustomHashTable<>(50);      // Fast lookup by election unique ID

    // Empty constructor
    public ElectionController() {}

    // --- CREATE/ADD FACILITIES ---

    /**
     * Adds a Politician to the master list and indexes them in the hash tables.
     */
    public void addPolitician(Politician p) {
        politicians.add(p); // Add to the sequential list
        polByName.insert(p.getName(), p); // Map name to object in hash table
        polByID.insert(p.getId(), p);     // Map ID to object in hash table
    }

    /**
     * Adds an Election to the master list and indexes it in the election hash table.
     */
    public void addElection(Election e) {
        elections.add(e); // Add to the sequential list
        elByID.insert(e.getId(), e); // Map ID to object in hash table
    }

    /**
     * Creates a CandidateEntry to link a politician to a specific election.
     * Validates that both the politician and election exist before adding.
     */
    public boolean addCandidateToElection(String polId, String electId, String party, int votes) {
        // Validation: Search hash tables to ensure IDs are valid
        Politician p = searchPoliticianByID(polId);
        Election e = searchElectionByID(electId);

        if (p != null && e != null) {
            // Create the new entry object
            CandidateEntry entry = new CandidateEntry(polId, electId, party, votes);
            allEntries.add(entry); // Record in the master link list
            // Record affiliation at the individual election level for drill-down functionality
            e.addCandidateEntry(entry);
            return true;
        }
        return false; // Returns false if IDs are not found
    }

    // --- SEARCH FACILITIES (Uses custom HashTable find method) ---

    /**
     * Finds a politician by name using O(1) hash table lookup.
     */
    public Politician searchPoliticianByName(String name) {
        return polByName.find(name);
    }

    /**
     * Finds a politician by ID using O(1) hash table lookup.
     */
    public Politician searchPoliticianByID(String id) {
        return polByID.find(id);
    }

    /**
     * Finds an election by ID using O(1) hash table lookup.
     */
    public Election searchElectionByID(String id) {
        return elByID.find(id);
    }

    // --- DELETE FACILITY ---

    /**
     * Deletes a politician and performs a cascading delete on all their candidate entries.
     */
    public void deletePolitician(String id) {
        // Iterate through master politician list
        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.get(i);
            if (p != null && p.getId().equals(id)) {
                politicians.remove(i); // Remove from master list

                // Cascading delete: Remove their entries from the master candidate list
                for (int j = allEntries.size() - 1; j >= 0; j--) {
                    CandidateEntry entry = allEntries.get(j);
                    if (entry != null && entry.getPoliticianId().equals(id)) {
                        allEntries.remove(j);
                    }
                }

                // Cascading delete: Remove them from individual election result lists
                for (int k = 0; k < elections.size(); k++) {
                    Election e = elections.get(k);
                    if (e != null) {
                        SimpleList<CandidateEntry> eList = e.getCandidateEntries();
                        for (int m = eList.size() - 1; m >= 0; m--) {
                            CandidateEntry ce = eList.get(m);
                            if (ce != null && ce.getPoliticianId().equals(id)) {
                                eList.remove(m);
                            }
                        }
                    }
                }

                // Refresh hash tables to remove deleted data
                rebuildIndexes();
                break;
            }
        }
    }

    /**
     * Deletes an election and rebuilds indexes to maintain data integrity.
     */
    public void deleteElection(String id) {
        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.get(i);
            if (e != null && e.getId().equals(id)) {
                elections.remove(i); // Remove from master list
                rebuildIndexes();    // Refresh hash tables
                break;
            }
        }
    }

    // --- UPDATE/EDIT FACILITIES ---

    /**
     * Updates the party and image URL of an existing politician.
     */
    public void updatePolitician(String id, String newParty, String newUrl) {
        Politician p = searchPoliticianByID(id); // Locate the object via hash table
        if (p != null) {
            p.setCurrentParty(newParty); // Set new party value
            p.setImageUrl(newUrl);       // Set new image URL
        }
    }

    // --- ADVANCED FILTERING ---

    /**
     * Filters the politician list by home county (ignoring case).
     */
    public SimpleList<Politician> filterPoliticiansByLocation(String county) {
        SimpleList<Politician> results = new SimpleList<>();
        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.get(i);
            if (p != null && p.getHomeCounty().equalsIgnoreCase(county)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Filters the election list by year.
     */
    public SimpleList<Election> filterElectionsByYear(int year) {
        SimpleList<Election> results = new SimpleList<>();
        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.get(i);
            if (e != null && e.getYear() == year) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Performs a linear search to find politicians whose name contains the search string.
     */
    public SimpleList<Politician> searchPoliticianByPartialName(String partName) {
        SimpleList<Politician> results = new SimpleList<>();
        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.get(i);
            if (p != null && p.getName().toLowerCase().contains(partName.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Sorts the master politician list alphabetically A-Z using Selection Sort.
     */
    public void sortPoliticiansByName() {
        int n = politicians.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i; // Assume current index is the minimum
            for (int j = i + 1; j < n; j++) {
                Politician pJ = politicians.get(j);
                Politician pMin = politicians.get(minIdx);

                if (pJ != null && pMin != null) {
                    if (pJ.getName().compareToIgnoreCase(pMin.getName()) < 0) {
                        minIdx = j;
                    }
                } else if (pJ != null && pMin == null) {
                    minIdx = j;
                }
            }
            // Swap the smallest found element with the current element
            Politician temp = politicians.get(minIdx);
            politicians.set(minIdx, politicians.get(i));
            politicians.set(i, temp);
        }
    }

    // --- PERSISTENCE ---

    /**
     * Serializes the master lists to an XML file.
     */
    public void save(String filename) throws Exception {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)))) {
            encoder.writeObject(politicians); // Save master politicians
            encoder.writeObject(elections);   // Save master elections
            encoder.writeObject(allEntries);  // Save master links
        }
    }

    /**
     * Deserializes XML data back into master lists and restores the system state.
     */
    @SuppressWarnings("unchecked")
    public void load(String filename) throws Exception {
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)))) {
            this.politicians = (SimpleList<Politician>) decoder.readObject();
            this.elections = (SimpleList<Election>) decoder.readObject();
            this.allEntries = (SimpleList<CandidateEntry>) decoder.readObject();

            // Clear internal candidate lists inside elections before re-linking
            for(int i = 0; i < elections.size(); i++) {
                Election e = elections.get(i);
                if (e != null) e.setCandidateEntries(new SimpleList<>());
            }
            rebuildIndexes(); // Re-populate hash tables and re-establish links
        }
    }

    /**
     * Sorts a specific list of CandidateEntry objects by votes (descending) using Selection Sort.
     */
    public void sortCandidatesByVotes(SimpleList<CandidateEntry> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i; // Assume current is the maximum
            for (int j = i + 1; j < n; j++) {
                CandidateEntry cJ = list.get(j);
                CandidateEntry cMax = list.get(maxIdx);

                if (cJ != null && cMax != null) {
                    // Descending order: highest votes first
                    if (cJ.getVotes() > cMax.getVotes()) {
                        maxIdx = j;
                    }
                } else if (cJ != null && cMax == null) {
                    maxIdx = j;
                }
            }
            // Swap the highest found element with the current element
            CandidateEntry temp = list.get(maxIdx);
            list.set(maxIdx, list.get(i));
            list.set(i, temp);
        }
    }

    /**
     * Filters the politician list by current party affiliation.
     */
    public SimpleList<Politician> filterPoliticiansByParty(String party) {
        SimpleList<Politician> results = new SimpleList<>();
        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.get(i);
            if (p != null && p.getCurrentParty().equalsIgnoreCase(party)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Completely refreshes the hash tables and re-links candidate records to election objects.
     * Essential for maintaining data integrity after a delete or load operation.
     */
    private void rebuildIndexes() {
        // 1. Reset the hash tables to start fresh
        polByName = new CustomHashTable<>(50);
        polByID = new CustomHashTable<>(50);
        elByID = new CustomHashTable<>(50);

        // 2. Re-insert politicians with a null check
        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.get(i);
            if (p != null) { // CRITICAL: Skip empty slots
                polByName.insert(p.getName(), p);
                polByID.insert(p.getId(), p);
            }
        }

        // 3. Re-insert elections with a null check
        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.get(i);
            if (e != null) { // CRITICAL: Skip empty slots
                elByID.insert(e.getId(), e);
            }
        }

        // 4. Re-link CandidateEntry links with null checks
        for (int i = 0; i < allEntries.size(); i++) {
            CandidateEntry entry = allEntries.get(i);
            if (entry != null) { // CRITICAL: Skip empty slots
                Election e = searchElectionByID(entry.getElectionId());
                if (e != null) {
                    e.addCandidateEntry(entry);
                }
            }
        }
    }

    // Getters for master lists
    public SimpleList<Politician> getPoliticians() { return politicians; }
    public SimpleList<Election> getElections() { return elections; }
}