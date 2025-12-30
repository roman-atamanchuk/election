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

    // 🔹 NEW: used for EDIT mode
    private Politician editingPolitician = null;

    // ================= EDIT MODE =================
    public void setPolitician(Politician p) {
        this.editingPolitician = p;

        idField.setText(p.getId());
        idField.setDisable(true); // ID must NOT change

        nameField.setText(p.getName());
        dobField.setText(p.getDateOfBirth());
        partyField.setText(p.getCurrentParty());
        countyField.setText(p.getHomeCounty());
        imageField.setText(p.getImageUrl());
    }

    // ================= SAVE =================
    @FXML
    private void onSave() {

        if (editingPolitician == null) {
            // ---------- ADD NEW ----------
            Politician p = new Politician(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    dobField.getText().trim(),
                    partyField.getText().trim(),
                    countyField.getText().trim(),
                    imageField.getText().trim()
            );
            controller.addPolitician(p);

        } else {
            // ---------- UPDATE EXISTING ----------
            editingPolitician.setName(nameField.getText().trim());
            editingPolitician.setDateOfBirth(dobField.getText().trim());
            editingPolitician.setCurrentParty(partyField.getText().trim());
            editingPolitician.setHomeCounty(countyField.getText().trim());
            editingPolitician.setImageUrl(imageField.getText().trim());
        }

        // ---------- SAVE TO XML ----------
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