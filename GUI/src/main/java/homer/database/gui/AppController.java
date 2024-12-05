package homer.database.gui;

import homer.database.backend.DataBase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AppController {
    @FXML
    private Label welcomeText;

    @FXML
    private void initialize() {
        DataBase.setPathToDataBase(AppProperties.PATH_TO_DATA_DIR, AppProperties.DB_NAME);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}