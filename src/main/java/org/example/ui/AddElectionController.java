package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.Election;
import org.example.model.ElectionType;

public class AddElectionController {

    @FXML private TextField idField;
    @FXML private TextField typeField;     // expects number 1–4
    @FXML private TextField yearField;
    @FXML private TextField locationField;
    @FXML private TextField seatsField;

    private final ElectionController controller =
            MainViewController.getController();

    @FXML

    private void onSave() {
        try {
            String typeText = typeField.getText().trim().toUpperCase();

            ElectionType type = ElectionType.valueOf(typeText);

            Election e = new Election(
                    idField.getText().trim(),
                    type,
                    Integer.parseInt(yearField.getText().trim()),
                    locationField.getText().trim(),
                    Integer.parseInt(seatsField.getText().trim())
            );

            controller.addElection(e);
            controller.save("data.xml");

            close();

        } catch (IllegalArgumentException ex) {
            showError("Type must be: GENERAL, LOCAL, EUROPEAN or PRESIDENTIAL");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Cannot save election");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}