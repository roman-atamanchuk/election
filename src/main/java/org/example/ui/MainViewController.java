package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.ElectionController;

import java.io.IOException;

public class MainViewController {

    @FXML
    private StackPane contentPane;

    // ONE shared controller for whole app
    private static final ElectionController controller = new ElectionController();

    // ================= INITIAL LOAD =================
    @FXML
    public void initialize() {

        // Load saved data ONCE
        try {
            controller.load("data.xml");
        } catch (Exception e) {
            System.out.println("No saved data found. Starting with empty data.");
        }

        // Default view
        showPoliticians();
    }

    // ================= TOP MENU =================
    @FXML
    public void showPoliticians() {
        loadView("politicians");
    }

    @FXML
    public void showElections() {
        loadView("elections");
    }

    @FXML
    public void showResults() {
        loadView("results");
    }

    // ================= ADD BUTTON =================
    @FXML
    private void onAdd() {

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                "Politician",
                "Politician",
                "Election",
                "Candidate"
        );

        dialog.setTitle("Add");
        dialog.setHeaderText("What do you want to add?");
        dialog.setContentText("Choose:");

        dialog.showAndWait().ifPresent(choice -> {
            switch (choice) {

                case "Politician" -> {
                    openAddWindow("add-politician");
                    showPoliticians(); // ✅ refresh
                }

                case "Election" -> {
                    openAddWindow("add-election");
                    showElections();   // ✅ refresh
                }

                case "Candidate" -> {
                    openAddWindow("add-candidate");
                    showElections();   // ✅ candidates belong to elections
                }
            }
        });
    }

    // ================= POPUP WINDOW =================
    private void openAddWindow(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/" + fxml + ".fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocks main window
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= VIEW LOADER =================
    private void loadView(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/" + viewName + ".fxml")
            );

            Parent view = loader.load();
            contentPane.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= SHARED DATA ACCESS =================
    public static ElectionController getController() {
        return controller;
    }
}