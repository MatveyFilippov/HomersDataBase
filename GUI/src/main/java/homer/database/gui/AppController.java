package homer.database.gui;

import homer.database.backend.DataBase;
import homer.database.gui.table.TableProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javax.naming.NameNotFoundException;
import java.io.IOException;

public class AppController {
    @FXML
    TableView<ObservableList<String>> table;

    @FXML
    private void initialize() throws NameNotFoundException, IOException {
        DataBase.setPathToDataBase(AppProperties.PATH_TO_DATA_DIR, AppProperties.DB_NAME);
        TableProcessor.init(table);
    }

}