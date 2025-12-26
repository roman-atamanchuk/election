package org.example.controller;

import org.example.model.*;
import org.example.util.SimpleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ElectionControllerTest {
    private ElectionController controller;

    @BeforeEach
    void setUp() {
        controller = new ElectionController();
    }

    // 1. Test addPolitician(Politician)
    @Test
    void testAddPolitician() {
        Politician p = new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1");
        controller.addPolitician(p);
        Politician retrieved = controller.getPoliticians().get(0);
        assertEquals("P1", retrieved.getId());
        assertEquals("Alice", retrieved.getName());
        assertEquals("1980", retrieved.getDateOfBirth());
        assertEquals("Green", retrieved.getCurrentParty());
        assertEquals("Waterford", retrieved.getHomeCounty());
        assertEquals("url1", retrieved.getImageUrl());
    }

    // 2. Test addElection(Election)
    @Test
    void testAddElection() {
        Election e = new Election("E1", ElectionType.GENERAL, 2024, "National", 3);
        controller.addElection(e);
        Election retrieved = controller.getElections().get(0);
        assertEquals("E1", retrieved.getId());
        assertEquals(ElectionType.GENERAL, retrieved.getType());
        assertEquals(2024, retrieved.getYear());
        assertEquals("National", retrieved.getLocation());
        assertEquals(3, retrieved.getSeats());
    }

    // 3. Test addCandidateToElection(String, String, String, int)
    @Test
    void testAddCandidateToElection() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        controller.addElection(new Election("E1", ElectionType.GENERAL, 2024, "National", 1));
        boolean success = controller.addCandidateToElection("P1", "E1", "Labour", 500);

        assertTrue(success);
        CandidateEntry ce = controller.searchElectionByID("E1").getCandidateEntries().get(0);
        assertEquals("P1", ce.getPoliticianId());
        assertEquals("E1", ce.getElectionId());
        assertEquals("Labour", ce.getPartyAtElection());
        assertEquals(500, ce.getVotes());
    }

    // 4. Test searchPoliticianByName(String)
    @Test
    void testSearchPoliticianByName() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        Politician p = controller.searchPoliticianByName("Alice");
        assertNotNull(p);
        assertEquals("P1", p.getId());
        assertEquals("Waterford", p.getHomeCounty());
    }

    // 5. Test searchPoliticianByID(String)
    @Test
    void testSearchPoliticianByID() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        Politician p = controller.searchPoliticianByID("P1");
        assertNotNull(p);
        assertEquals("Alice", p.getName());
    }

    // 6. Test searchElectionByID(String)
    @Test
    void testSearchElectionByID() {
        controller.addElection(new Election("E1", ElectionType.GENERAL, 2024, "National", 1));
        Election e = controller.searchElectionByID("E1");
        assertNotNull(e);
        assertEquals(2024, e.getYear());
    }

    // 7. Test deletePolitician(String)
    @Test
    void testDeletePolitician() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        controller.deletePolitician("P1");
        assertNull(controller.searchPoliticianByID("P1"));
        assertEquals(0, controller.getPoliticians().size());
    }

    // 8. Test deleteElection(String)
    @Test
    void testDeleteElection() {
        controller.addElection(new Election("E1", ElectionType.GENERAL, 2024, "National", 1));
        controller.deleteElection("E1");
        assertNull(controller.searchElectionByID("E1"));
        assertEquals(0, controller.getElections().size());
    }

    // 9. Test updatePolitician(String, String, String)
    @Test
    void testUpdatePolitician() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        controller.updatePolitician("P1", "Independent", "new_url_2");
        Politician p = controller.searchPoliticianByID("P1");
        assertEquals("Independent", p.getCurrentParty());
        assertEquals("new_url_2", p.getImageUrl());
        assertEquals("Alice", p.getName()); // Name should remain unchanged
    }

    // 10. Test filterPoliticiansByLocation(String)
    @Test
    void testFilterPoliticiansByLocation() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        SimpleList<Politician> list = controller.filterPoliticiansByLocation("Waterford");
        assertEquals(1, list.size());
        assertEquals("P1", list.get(0).getId());
    }

    // 11. Test filterElectionsByYear(int)
    @Test
    void testFilterElectionsByYear() {
        controller.addElection(new Election("E1", ElectionType.LOCAL, 2022, "Waterford", 2));
        SimpleList<Election> list = controller.filterElectionsByYear(2022);
        assertEquals(1, list.size());
        assertEquals(ElectionType.LOCAL, list.get(0).getType());
    }

    // 12. Test searchPoliticianByPartialName(String)
    @Test
    void testSearchPoliticianByPartialName() {
        controller.addPolitician(new Politician("P1", "Mary Lou", "1970", "SF", "Dublin", "url"));
        SimpleList<Politician> results = controller.searchPoliticianByPartialName("Lou");
        assertEquals(1, results.size());
        assertEquals("Mary Lou", results.get(0).getName());
    }

    // 13. Test sortPoliticiansByName()
    @Test
    void testSortPoliticiansByName() {
        controller.addPolitician(new Politician("P1", "Zack", "1990", "Ind", "Cork", "u"));
        controller.addPolitician(new Politician("P2", "Aaron", "1991", "Ind", "Cork", "u"));
        controller.sortPoliticiansByName();
        assertEquals("Aaron", controller.getPoliticians().get(0).getName());
        assertEquals("Zack", controller.getPoliticians().get(1).getName());
    }

    // 14. Test save(String)
    @Test
    void testSave() throws Exception {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url"));
        controller.save("coverage_test.xml");
        File file = new File("coverage_test.xml");
        assertTrue(file.exists() && file.length() > 0);
        file.delete();
    }

    // 15. Test load(String)
    @Test
    void testLoad() throws Exception {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url"));
        controller.save("coverage_test_load.xml");
        ElectionController newCtrl = new ElectionController();
        newCtrl.load("coverage_test_load.xml");

        Politician p = newCtrl.searchPoliticianByID("P1");
        assertEquals("Alice", p.getName());
        assertEquals("Green", p.getCurrentParty());
        new File("coverage_test_load.xml").delete();
    }

    // 16. Test sortCandidatesByVotes(SimpleList)
    @Test
    void testSortCandidatesByVotes() {
        SimpleList<CandidateEntry> list = new SimpleList<>();
        list.add(new CandidateEntry("P1", "E1", "Ind", 100));
        list.add(new CandidateEntry("P2", "E1", "Ind", 999));
        list.add(new CandidateEntry("P3", "E1", "Ind", 500));

        controller.sortCandidatesByVotes(list);
        assertEquals(999, list.get(0).getVotes());
        assertEquals(500, list.get(1).getVotes());
        assertEquals(100, list.get(2).getVotes());
    }

    // 17. Test filterPoliticiansByParty(String)
    @Test
    void testFilterPoliticiansByParty() {
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Labour", "Waterford", "url"));
        SimpleList<Politician> list = controller.filterPoliticiansByParty("Labour");
        assertEquals(1, list.size());
        assertEquals("Alice", list.get(0).getName());
    }

    // 18. Test rebuildIndexes()
    @Test
    void testRebuildIndexes() {
        // rebuildIndexes is private but critical for search reliability after data changes
        controller.addPolitician(new Politician("P1", "Alice", "1980", "Green", "Waterford", "url1"));
        controller.deletePolitician("P1"); // This calls rebuildIndexes internally

        // Assert hash tables are cleared and correctly reflect the empty state
        assertNull(controller.searchPoliticianByID("P1"));
        assertNull(controller.searchPoliticianByName("Alice"));
    }
}