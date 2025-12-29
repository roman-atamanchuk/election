package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.SimpleStringProperty;

import org.example.controller.ElectionController;
import org.example.model.*;

public class ElectionsController {

    // ---------- LEFT: ELECTION LIST ----------
    @FXML private TableView<Election> electionsTable;
    @FXML private TableColumn<Election, String> elIdCol;
    @FXML private TableColumn<Election, String> elYearCol;
    @FXML private TableColumn<Election, String> elTypeCol;
    @FXML private TableColumn<Election, String> elLocationCol;
    @FXML private TableColumn<Election, String> elSeatsCol;

    // ---------- CENTER: CANDIDATES ----------
    @FXML private TableView<CandidateEntry> candidatesTable;
    @FXML private TableColumn<CandidateEntry, String> candIdCol;
    @FXML private TableColumn<CandidateEntry, String> candNameCol;
    @FXML private TableColumn<CandidateEntry, String> candPartyCol;

    // ---------- RIGHT: POLITICIAN DETAILS ----------
    @FXML private ImageView photoView;
    @FXML private Label nameLabel;
    @FXML private Label dobLabel;
    @FXML private Label partyLabel;
    @FXML private Label countyLabel;

    // Shared logic controller
    private final ElectionController controller =
            MainViewController.getController();

    @FXML
    public void initialize() {

        /* =====================================================
         * LEFT TABLE – ELECTIONS
         * ===================================================== */

        elIdCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getId()));

        elYearCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getYear())));

        elTypeCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getType().toString()));

        elLocationCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getLocation()));

        elSeatsCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getSeats())));

        // Load elections from SimpleList
        electionsTable.getItems().clear();
        for (int i = 0; i < controller.getElections().size(); i++) {
            electionsTable.getItems().add(
                    controller.getElections().get(i)
            );
        }

        // Election selection listener
        electionsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) ->
                        loadElection(newVal));

        /* =====================================================
         * CENTER TABLE – CANDIDATES
         * ===================================================== */

        candIdCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getPoliticianId()));

        candPartyCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getPartyAtElection()));

        candNameCol.setCellValueFactory(d -> {
            Politician p =
                    controller.searchPoliticianByID(
                            d.getValue().getPoliticianId()
                    );
            return new SimpleStringProperty(
                    p != null ? p.getName() : "Unknown"
            );
        });

        // Candidate selection listener
        candidatesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) ->
                        showPoliticianDetails(newVal));

        /* =====================================================
         * AUTO-SELECT FIRST ELECTION
         * ===================================================== */

        if (!electionsTable.getItems().isEmpty()) {
            electionsTable.getSelectionModel().select(0);
        } else {
            clearPoliticianDetails();
        }
    }

    /* =========================================================
     * LOAD ELECTION → CENTER TABLE
     * ========================================================= */

    private void loadElection(Election e) {
        if (e == null) return;

        candidatesTable.getItems().clear();
        candidatesTable.getSelectionModel().clearSelection();

        for (int i = 0; i < e.getCandidateEntries().size(); i++) {
            candidatesTable.getItems().add(
                    e.getCandidateEntries().get(i)
            );
        }

        // Auto-select first candidate
        if (!candidatesTable.getItems().isEmpty()) {
            candidatesTable.getSelectionModel().select(0);
        } else {
            clearPoliticianDetails();
        }
    }

    /* =========================================================
     * RIGHT PANEL – POLITICIAN DETAILS
     * ========================================================= */

    private void showPoliticianDetails(CandidateEntry ce) {
        if (ce == null) {
            clearPoliticianDetails();
            return;
        }

        Politician p =
                controller.searchPoliticianByID(
                        ce.getPoliticianId()
                );

        if (p == null) {
            clearPoliticianDetails();
            return;
        }

        nameLabel.setText("Name: " + p.getName());
        dobLabel.setText("DOB: " + p.getDateOfBirth());
        partyLabel.setText("Party: " + p.getCurrentParty());
        countyLabel.setText("County: " + p.getHomeCounty());

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            photoView.setImage(new Image(p.getImageUrl(), true));
        } else {
            photoView.setImage(null);
        }
    }

    private void clearPoliticianDetails() {
        nameLabel.setText("Name:");
        dobLabel.setText("DOB:");
        partyLabel.setText("Party:");
        countyLabel.setText("County:");
        photoView.setImage(null);
    }
}