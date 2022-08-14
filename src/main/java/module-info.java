module snake {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.koubae to javafx.fxml;
    exports com.koubae;
}