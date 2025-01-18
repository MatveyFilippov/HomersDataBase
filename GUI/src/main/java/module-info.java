module homer.database.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires HomerDataBase.backend;
    requires java.naming;
    requires HomerDataBase.convertor;


    opens homer.database.gui to javafx.fxml;
    exports homer.database.gui;
    exports homer.database.gui.misc;
    opens homer.database.gui.misc to javafx.fxml;
    exports homer.database.gui.table;
    opens homer.database.gui.table to javafx.fxml;
}