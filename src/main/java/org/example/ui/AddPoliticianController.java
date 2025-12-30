package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.Politician;

public class AddPoliticianController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField dobField;
    @FXML private TextField partyField;
    @FXML private TextField countyField;
    @FXML private TextField imageField;

    private final ElectionController controller =
            MainViewController.getController();

    @FXML
    private void onSave() {

        Politician p = new Politician(
                idField.getText().trim(),
                nameField.getText().trim(),
                dobField.getText().trim(),
                partyField.getText().trim(),
                countyField.getText().trim(),
                imageField.getText().trim()
        );

        // ✅ add to memory
        controller.addPolitician(p);

        // ✅ SAVE TO XML (THIS WAS MISSING)
        try {
            controller.save("data.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}
