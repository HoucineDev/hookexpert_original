module com.app.ancea {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.prefs;
    requires java.desktop;


    opens com.app.ancea to javafx.fxml;
    exports com.app.ancea;
}