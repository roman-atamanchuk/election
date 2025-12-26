//module org.example {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires java.desktop;
//
//    opens org.example to javafx.fxml;
//    exports org.example;
//}
module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // Allows the use of XMLEncoder/Decoder

    // Allows JavaFX to see your controllers for the GUI
    opens org.example to javafx.fxml;

    // CRITICAL: Allows XML tools to access your data for Saving/Loading
    opens org.example.model to java.desktop;
    opens org.example.util to java.desktop;
    opens org.example.controller to java.desktop;

    // Standard exports so other parts of Java can use your code
    exports org.example;
    exports org.example.model;
    exports org.example.util;
}
