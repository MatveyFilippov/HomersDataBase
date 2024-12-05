module homer.database.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires HomersDataBase.backend;
    requires java.naming;


    opens homer.database.gui to javafx.fxml;
    exports homer.database.gui;
    exports homer.database.gui.misc;
    opens homer.database.gui.misc to javafx.fxml;
    exports homer.database.gui.table;
    opens homer.database.gui.table to javafx.fxml;
}