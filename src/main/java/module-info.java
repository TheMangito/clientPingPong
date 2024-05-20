module com.example.testjavafxserverclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.testjavafxserverclient to javafx.fxml;
    exports com.example.testjavafxserverclient;
}