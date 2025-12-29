package org.example.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.example.controller.ElectionController;

import java.io.IOException;

public class MainViewController {

    @FXML
    private StackPane contentPane;

    // ONE shared controller for whole app
    private static final ElectionController controller = new ElectionController();

    // Called automatically when main.fxml is loaded
    @FXML
    public void initialize() {

        // 🔹 Load saved data ONCE (if exists)
        try {
            controller.load("data.xml");
        } catch (Exception e) {
            System.out.println("No saved data found. Starting with empty data.");
        }

        // 🔹 Default view
        showPoliticians();
    }

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

    // Helper method to load any view into center pane
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

    // 🔹 Allow child controllers to access data
    public static ElectionController getController() {
        return controller;
    }
}