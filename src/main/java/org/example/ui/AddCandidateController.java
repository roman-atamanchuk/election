package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.controller.ElectionController;
import org.example.model.Election;
import org.example.model.Politician;

public class AddCandidateController {

    @FXML private ComboBox<Politician> politicianBox;
    @FXML private ComboBox<Election> electionBox;
    @FXML private TextField partyField;
    @FXML private TextField votesField;

    private final ElectionController controller =
            MainViewController.getController();

    @FXML
    public void initialize() {
        // load politicians
        for (int i = 0; i < controller.getPoliticians().size(); i++) {
            politicianBox.getItems().add(controller.getPoliticians().get(i));
        }

        // load elections
        for (int i = 0; i < controller.getElections().size(); i++) {
            electionBox.getItems().add(controller.getElections().get(i));
        }
    }

    @FXML
    private void onSave() {
        try {
            Politician p = politicianBox.getValue();
            Election e = electionBox.getValue();

            if (p == null || e == null) return;

            String party = partyField.getText().trim();
            int votes = Integer.parseInt(votesField.getText().trim());

            controller.addCandidateToElection(
                    p.getId(),
                    e.getId(),
                    party,
                    votes
            );

            controller.save("data.xml");

            close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) politicianBox.getScene().getWindow();
        stage.close();
    }
}
