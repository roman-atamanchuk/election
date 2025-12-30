package org.example.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.Politician;
import org.example.util.SimpleList;

public class PoliticiansController {

    // SEARCH + FILTER
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> filterTypeBox;
    @FXML private TextField filterValueField;

    // TABLE
    @FXML private TableView<Politician> table;
    @FXML private TableColumn<Politician, String> idCol;
    @FXML private TableColumn<Politician, String> nameCol;

    // DETAILS
    @FXML private ImageView photoView;
    @FXML private Label nameLabel;
    @FXML private Label dobLabel;
    @FXML private Label partyLabel;
    @FXML private Label countyLabel;

    // SHARED CONTROLLER
    private final ElectionController controller =
            MainViewController.getController();

    @FXML
    public void initialize() {

        // TABLE BINDINGS
        idCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getId())
        );

        nameCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getName())
        );

        // FILTER OPTIONS
        filterTypeBox.getItems().addAll("Party", "County");
        filterTypeBox.setValue("Party");

        // RIGHT-CLICK MENU
        addContextMenu();

        // LOAD DATA
        loadPoliticians();

        // SELECTION LISTENER
        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, o, n) -> showDetails(n));
    }

    // ================= CONTEXT MENU =================
    private void addContextMenu() {

        table.setRowFactory(tv -> {
            TableRow<Politician> row = new TableRow<>();

            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");

            edit.setOnAction(e -> editPolitician(row.getItem()));
            delete.setOnAction(e -> deletePolitician(row.getItem()));

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

    // ================= LOAD =================
    private void loadPoliticians() {
        table.getItems().clear();

        SimpleList<Politician> list = controller.getPoliticians();
        for (int i = 0; i < list.size(); i++) {
            table.getItems().add(list.get(i));
        }

        if (!table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
            showDetails(table.getItems().get(0));
            Platform.runLater(() -> table.requestFocus());
        }
    }

    // ================= SEARCH =================
    @FXML
    private void onSearch() {
        String text = searchField.getText().trim();
        table.getItems().clear();

        if (text.isEmpty()) {
            loadPoliticians();
            return;
        }

        var results = controller.searchPoliticianByPartialName(text);
        for (int i = 0; i < results.size(); i++) {
            table.getItems().add(results.get(i));
        }

        if (!table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
        } else {
            clearDetails();
        }
    }

    // ================= FILTER =================
    @FXML
    private void applyFilter() {
        String value = filterValueField.getText().trim();
        if (value.isEmpty()) return;

        table.getItems().clear();

        var list = filterTypeBox.getValue().equals("Party")
                ? controller.filterPoliticiansByParty(value)
                : controller.filterPoliticiansByLocation(value);

        for (int i = 0; i < list.size(); i++) {
            table.getItems().add(list.get(i));
        }

        if (!table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
        } else {
            clearDetails();
        }
    }

    @FXML
    private void clearFilter() {
        searchField.clear();
        filterValueField.clear();
        loadPoliticians();
    }

    // ================= DETAILS =================
    private void showDetails(Politician p) {
        if (p == null) {
            clearDetails();
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

    private void clearDetails() {
        nameLabel.setText("Name:");
        dobLabel.setText("DOB:");
        partyLabel.setText("Party:");
        countyLabel.setText("County:");
        photoView.setImage(null);
    }

    // ================= DELETE =================
    private void deletePolitician(Politician p) {
        if (p == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete " + p.getName() + "?");

        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                controller.deletePolitician(p.getId());
                try {
                    controller.save("data.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadPoliticians();
            }
        });
    }

    // ================= EDIT =================
    private void editPolitician(Politician p) {
        if (p == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/add-politician.fxml")
            );

            Parent root = loader.load();
            AddPoliticianController c = loader.getController();
            c.setPolitician(p);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            controller.save("data.xml");
            loadPoliticians();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}