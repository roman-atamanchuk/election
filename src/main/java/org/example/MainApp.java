package org.example;

import org.example.controller.ElectionController;
import org.example.model.*;
import org.example.util.SimpleList;
import java.util.Scanner;
//ki
public class MainApp {
    private ElectionController controller;
    private Scanner input;

    public MainApp() {
        controller = new ElectionController();
        input = new Scanner(System.in);
    }

    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.runMenu();
    }

    private int mainMenu() {
        System.out.println("\n--- ELECTION MANAGEMENT SYSTEM (CA2) ---");
        System.out.println("1)  Add Politician / Election / Candidate");
        System.out.println("2)  Edit/Update Politician");
        System.out.println("3)  Delete Politician / Election");
        System.out.println("----------------------------------------");
        System.out.println("4)  Search Politicians (Partial Name)");
        System.out.println("5)  Filter Politicians (by Party or County)");
        System.out.println("6)  Filter Elections (by Year)");
        System.out.println("7)  VIEW ELECTION RESULTS (Ranked + Winners)"); // Requirement: Drill Down & Winners
        System.out.println("----------------------------------------");
        System.out.println("8)  List All Politicians (Sorted by Name)");
        System.out.println("9)  Save/Load Data (XML)");
        System.out.println("0)  Exit");
        System.out.print("==>> ");

        while (!input.hasNextInt()) {
            System.out.println("Please enter a number.");
            input.next();
        }
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }

    private void runMenu() {
        int choice = mainMenu();
        while (choice != 0) {
            switch (choice) {
                case 1 -> addMenu();
                case 2 -> editPolitician();
                case 3 -> deleteMenu();
                case 4 -> searchPartialName();
                case 5 -> filterPoliticianMenu();
                case 6 -> filterElectionYear();
                case 7 -> viewElectionResults();
                case 8 -> listPoliticians();
                case 9 -> persistenceMenu();
                default -> System.out.println("Invalid option.");
            }
            choice = mainMenu();
        }
    }

    // --- 1. ADD MENU ---
    private void addMenu() {
        System.out.println("a) Add Politician\nb) Add Election\nc) Add Candidate to Election");
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("a")) addPolitician();
        else if (choice.equalsIgnoreCase("b")) addElection();
        else if (choice.equalsIgnoreCase("c")) addCandidateToElection();
    }

    // --- 2. EDIT POLITICIAN ---
    private void editPolitician() {
        System.out.print("Enter Politician ID to edit: ");
        String id = input.nextLine();
        System.out.print("Enter New Party: ");
        String party = input.nextLine();
        System.out.print("Enter New Image URL: ");
        String url = input.nextLine();
        controller.updatePolitician(id, party, url);
        System.out.println("Update attempted.");
    }

    // --- 3. DELETE MENU ---
    private void deleteMenu() {
        System.out.println("a) Delete Politician\nb) Delete Election");
        String choice = input.nextLine();
        if (choice.equalsIgnoreCase("a")) {
            System.out.print("Enter ID: ");
            controller.deletePolitician(input.nextLine());
        } else if (choice.equalsIgnoreCase("b")) {
            System.out.print("Enter ID: ");
            controller.deleteElection(input.nextLine());
        }
    }

    // --- 4. PARTIAL SEARCH ---
    private void searchPartialName() {
        System.out.print("Enter name snippet: ");
        String part = input.nextLine();
        SimpleList<Politician> results = controller.searchPoliticianByPartialName(part);
        displayList(results);
    }

    // --- 5. FILTER POLITICIAN ---
    private void filterPoliticianMenu() {
        System.out.println("Filter by: 1) Party  2) County");
        int c = input.nextInt(); input.nextLine();
        if (c == 1) {
            System.out.print("Enter Party: ");
            displayList(controller.filterPoliticiansByParty(input.nextLine()));
        } else {
            System.out.print("Enter County: ");
            displayList(controller.filterPoliticiansByLocation(input.nextLine()));
        }
    }

    // --- 6. FILTER ELECTION ---
    private void filterElectionYear() {
        System.out.print("Enter Year: ");
        int year = input.nextInt(); input.nextLine();
        SimpleList<Election> results = controller.filterElectionsByYear(year);
        for(int i=0; i<results.size(); i++) System.out.println(results.get(i));
    }

    // --- 7. VIEW RESULTS (Ranked + Winners Highlighted) ---
    private void viewElectionResults() {
        System.out.print("Enter Election ID: ");
        String id = input.nextLine();
        Election e = controller.searchElectionByID(id);
        if (e != null) {
            System.out.println("\n--- RESULTS: " + e.getType() + " (" + e.getYear() + ") ---");
            System.out.println("Seats available: " + e.getSeats());

            SimpleList<CandidateEntry> candidates = e.getCandidateEntries();
            // Requirement: Sort candidates by votes descending
            controller.sortCandidatesByVotes(candidates);



            for (int i = 0; i < candidates.size(); i++) {
                CandidateEntry ce = candidates.get(i);
                Politician p = controller.searchPoliticianByID(ce.getPoliticianId());
                String pName = (p != null) ? p.getName() : "Unknown";

                // Requirement: Highlight the winning candidates based on number of seats
                if (i < e.getSeats()) {
                    System.out.println(">> WINNER [Seat " + (i + 1) + "]: " + pName + " (" + ce.getVotes() + " votes)");
                } else {
                    System.out.println("   Rank " + (i + 1) + ": " + pName + " (" + ce.getVotes() + " votes)");
                }
            }
        } else {
            System.out.println("Election not found.");
        }
    }

    // --- HELPERS ---
    private void displayList(SimpleList<Politician> list) {
        if (list.size() == 0) System.out.println("No records found.");
        for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
    }

    private void listPoliticians() {
        controller.sortPoliticiansByName();
        displayList(controller.getPoliticians());
    }

    private void persistenceMenu() {
        System.out.println("1) Save  2) Load");
        int c = input.nextInt(); input.nextLine();
        try {
            if (c == 1) controller.save("data.xml");
            else controller.load("data.xml");
            System.out.println("Operation successful.");
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    // (Add the private addPolitician/addElection/addCandidateToElection methods from your old code here)
    private void addPolitician() {
        System.out.print("ID: "); String id = input.nextLine();
        System.out.print("Name: "); String name = input.nextLine();
        System.out.print("DOB: "); String dob = input.nextLine();
        System.out.print("Party: "); String party = input.nextLine();
        System.out.print("County: "); String county = input.nextLine();
        System.out.print("URL: "); String url = input.nextLine();
        controller.addPolitician(new Politician(id, name, dob, party, county, url));
    }

    private void addElection() {
        System.out.print("ID: "); String id = input.nextLine();
        System.out.println("1)General 2)Local 3)European 4)Presidential");
        int c = input.nextInt(); input.nextLine();
        ElectionType t = switch(c) { case 2->ElectionType.LOCAL; case 3->ElectionType.EUROPEAN; case 4->ElectionType.PRESIDENTIAL; default->ElectionType.GENERAL; };
        System.out.print("Year: "); int y = input.nextInt(); input.nextLine();
        System.out.print("Loc: "); String loc = input.nextLine();
        System.out.print("Seats: "); int s = input.nextInt(); input.nextLine();
        controller.addElection(new Election(id, t, y, loc, s));
    }

    private void addCandidateToElection() {
        System.out.print("Pol ID: "); String pId = input.nextLine();
        System.out.print("Elec ID: "); String eId = input.nextLine();
        System.out.print("Party: "); String party = input.nextLine();
        System.out.print("Votes: "); int v = input.nextInt(); input.nextLine();
        if(controller.addCandidateToElection(pId, eId, party, v)) System.out.println("Added.");
        else System.out.println("Error: IDs not found.");
    }
}