package org.example;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static HostServices hostServices; // ✅ ADD

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();       // ✅ ADD
        scene = new Scene(loadFXML("view/main"), 800, 550);
        stage.setScene(scene);
        stage.setTitle("Elections Information System");
        stage.setMaximized(true);
        stage.show();
    }

    // ✅ ADD: allow controllers to access HostServices
    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}