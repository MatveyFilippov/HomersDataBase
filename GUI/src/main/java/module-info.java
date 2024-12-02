module homer.database.gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens homer.database.gui to javafx.fxml;
    exports homer.database.gui;
}