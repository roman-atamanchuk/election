package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.controller.ElectionController;
import org.example.model.*;
import org.example.util.SimpleList;

public class ResultsController {

    // ---------- LEFT: ELECTIONS ----------
    @FXML private TableView<Election> electionsTable;
    @FXML private TableColumn<Election, String> elIdCol;
    @FXML private TableColumn<Election, String> elYearCol;
    @FXML private TableColumn<Election, String> elTypeCol;
    @FXML private TableColumn<Election, String> elLocationCol;
    @FXML private TableColumn<Election, String> elSeatsCol;

    // ---------- CENTER: RESULTS ----------
    @FXML private TableView<CandidateEntry> resultsTable;
    @FXML private TableColumn<CandidateEntry, String> candIdCol;
    @FXML private TableColumn<CandidateEntry, String> candNameCol;
    @FXML private TableColumn<CandidateEntry, String> candPartyCol;
    @FXML private TableColumn<CandidateEntry, String> votesCol;

    // ---------- RIGHT: POLITICIAN DETAILS ----------
    @FXML private ImageView photoView;
    @FXML private Label nameLabel;
    @FXML private Label dobLabel;
    @FXML private Label partyLabel;
    @FXML private Label countyLabel;

    private final ElectionController controller =
            MainViewController.getController();

    private Election currentElection;

    @FXML
    public void initialize() {

        // ----- LEFT TABLE (ELECTIONS) -----
        elIdCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));

        elYearCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getYear())));

        elTypeCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getType().toString()));

        elLocationCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getLocation()));

        elSeatsCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getSeats())));

        electionsTable.getItems().clear();
        for (int i = 0; i < controller.getElections().size(); i++) {
            electionsTable.getItems().add(
                    controller.getElections().get(i)
            );
        }

        electionsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> loadResults(n));

        // ----- CENTER TABLE (RESULTS) -----
        candIdCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getPoliticianId()));

        candPartyCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getPartyAtElection()));

        candNameCol.setCellValueFactory(d -> {
            Politician p =
                    controller.searchPoliticianByID(
                            d.getValue().getPoliticianId());
            return new javafx.beans.property.SimpleStringProperty(
                    p != null ? p.getName() : "Unknown");
        });

        votesCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(d.getValue().getVotes())));

        resultsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> showPoliticianDetails(n));

        // ----- AUTO SELECT FIRST ELECTION -----
        if (!electionsTable.getItems().isEmpty()) {
            electionsTable.getSelectionModel().select(0);
        } else {
            clearPoliticianDetails();
        }
    }

    // ---------- LOAD RESULTS ----------
    private void loadResults(Election e) {
        if (e == null) return;

        currentElection = e;
        resultsTable.getItems().clear();

        SimpleList<CandidateEntry> copy = new SimpleList<>();
        for (int i = 0; i < e.getCandidateEntries().size(); i++) {
            copy.add(e.getCandidateEntries().get(i));
        }

        controller.sortCandidatesByVotes(copy);

        for (int i = 0; i < copy.size(); i++) {
            resultsTable.getItems().add(copy.get(i));
        }

        if (!resultsTable.getItems().isEmpty()) {
            resultsTable.getSelectionModel().select(0);
        } else {
            clearPoliticianDetails();
        }
    }

    // ---------- RIGHT PANEL ----------
    private void showPoliticianDetails(CandidateEntry ce) {
        if (ce == null) {
            clearPoliticianDetails();
            return;
        }

        Politician p =
                controller.searchPoliticianByID(
                        ce.getPoliticianId());

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