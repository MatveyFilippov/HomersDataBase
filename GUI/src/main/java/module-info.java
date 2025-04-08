module homer.database.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires org.slf4j;
    requires java.logging;
    requires converter.beta;
    requires backend;


    opens homer.database.gui to javafx.fxml;
    exports homer.database.gui;
    exports homer.database.gui.misc;
    opens homer.database.gui.misc to javafx.fxml;
}