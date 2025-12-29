module org.example {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // XMLEncoder / XMLDecoder

    // JavaFX UI controllers (FXML reflection)
    opens org.example.ui to javafx.fxml;

    // JavaFX app class
    opens org.example to javafx.fxml;

    // XML persistence (reflection)
    opens org.example.model to java.desktop;
    opens org.example.util to java.desktop;
    opens org.example.controller to java.desktop;

    // Normal exports
    exports org.example;
    exports org.example.model;
    exports org.example.util;
}