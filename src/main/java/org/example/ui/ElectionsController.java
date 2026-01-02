package org.example.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.*;

public class ElectionsController {
    // ---------- SEARCH ----------
    @FXML private TextField yearSearchField;
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

    private final ElectionController controller =
            MainViewController.getController();

    @FXML
    public void initialize() {

        /* ================= LEFT TABLE ================= */

        elIdCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getId()));

        elYearCol.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getYear())));

        elTypeCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType().toString()));

        elLocationCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getLocation()));

        elSeatsCol.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getSeats())));

        addElectionContextMenu();
        loadElections();

        electionsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> loadElection(n));

        /* ================= CENTER TABLE ================= */

        candIdCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPoliticianId()));

        candPartyCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPartyAtElection()));

        candNameCol.setCellValueFactory(d -> {
            Politician p = controller.searchPoliticianByID(
                    d.getValue().getPoliticianId());
            return new SimpleStringProperty(
                    p != null ? p.getName() : "Unknown");
        });

        candidatesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> showPoliticianDetails(n));

        if (!electionsTable.getItems().isEmpty()) {
            electionsTable.getSelectionModel().select(0);
        }
    }

    /* ================= CONTEXT MENU ================= */

    private void addElectionContextMenu() {
        electionsTable.setRowFactory(tv -> {
            TableRow<Election> row = new TableRow<>();

            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");

            edit.setOnAction(e -> editElection(row.getItem()));
            delete.setOnAction(e -> deleteElection(row.getItem()));

            ContextMenu menu = new ContextMenu(edit, delete);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );
            return row;
        });
    }

    /* ================= LOAD ================= */

    private void loadElections() {
        electionsTable.getItems().clear();
        for (int i = 0; i < controller.getElections().size(); i++) {
            electionsTable.getItems().add(
                    controller.getElections().get(i));
        }
    }

    private void loadElection(Election e) {
        if (e == null) return;

        candidatesTable.getItems().clear();
        for (int i = 0; i < e.getCandidateEntries().size(); i++) {
            candidatesTable.getItems().add(
                    e.getCandidateEntries().get(i));
        }

        if (!candidatesTable.getItems().isEmpty()) {
            candidatesTable.getSelectionModel().select(0);
        } else {
            clearPoliticianDetails();
        }
    }

    /* ================= RIGHT PANEL ================= */

    private void showPoliticianDetails(CandidateEntry ce) {
        if (ce == null) {
            clearPoliticianDetails();
            return;
        }

        Politician p =
                controller.searchPoliticianByID(ce.getPoliticianId());

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

    /* ================= DELETE ================= */

    private void deleteElection(Election e) {
        if (e == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete election " + e.getId() + "?");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                controller.deleteElection(e.getId());
                try {
                    controller.save("data.xml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                loadElections();
            }
        });
    }

    /* ================= EDIT ================= */

    private void editElection(Election e) {
        if (e == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/add-election.fxml"));

            Parent root = loader.load();
            AddElectionController c = loader.getController();
            c.setElection(e);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            controller.save("data.xml");
            loadElections();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /* ================= SEARCH ================= */

    @FXML
    private void onSearchByYear() {
        String text = yearSearchField.getText().trim();

        if (text.isEmpty()) {
            loadElections();
            return;
        }

        try {
            int year = Integer.parseInt(text);

            electionsTable.getItems().clear();
            var results = controller.filterElectionsByYear(year);

            for (int i = 0; i < results.size(); i++) {
                electionsTable.getItems().add(results.get(i));
            }

            if (!electionsTable.getItems().isEmpty()) {
                electionsTable.getSelectionModel().select(0);
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid year");
            alert.setContentText("Year must be a number");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClearSearch() {
        yearSearchField.clear();
        loadElections();
    }
}