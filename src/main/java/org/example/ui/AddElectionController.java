package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.Election;
import org.example.model.ElectionType;

public class AddElectionController {

    @FXML private TextField idField;
    @FXML private ChoiceBox<ElectionType> typeBox;
    @FXML private TextField yearField;
    @FXML private TextField locationField;
    @FXML private TextField seatsField;

    private final ElectionController controller =
            MainViewController.getController();

    // 🔹 EDIT MODE SUPPORT
    private Election editingElection = null;

    @FXML
    public void initialize() {
        typeBox.getItems().setAll(ElectionType.values());
        typeBox.setValue(ElectionType.GENERAL);
    }

    // ================= EDIT MODE =================
    public void setElection(Election e) {
        this.editingElection = e;

        idField.setText(e.getId());
        idField.setDisable(true); // ID must not change

        typeBox.setValue(e.getType());
        yearField.setText(String.valueOf(e.getYear()));
        locationField.setText(e.getLocation());
        seatsField.setText(String.valueOf(e.getSeats()));
    }

    // ================= SAVE =================
    @FXML
    private void onSave() {
        try {
            if (editingElection == null) {
                // ---------- ADD NEW ----------
                Election e = new Election(
                        idField.getText().trim(),
                        typeBox.getValue(),
                        Integer.parseInt(yearField.getText().trim()),
                        locationField.getText().trim(),
                        Integer.parseInt(seatsField.getText().trim())
                );
                controller.addElection(e);

            } else {
                // ---------- UPDATE EXISTING ----------
                editingElection.setType(typeBox.getValue());
                editingElection.setYear(
                        Integer.parseInt(yearField.getText().trim()));
                editingElection.setLocation(locationField.getText().trim());
                editingElection.setSeats(
                        Integer.parseInt(seatsField.getText().trim()));
            }

            controller.save("data.xml");
            close();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) idField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Cannot save election");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}