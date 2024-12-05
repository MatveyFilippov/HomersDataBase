package homer.database.gui;

import homer.database.gui.misc.ErrorLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class DataBaseApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(ErrorLogger::appErrorHandler);

        FXMLLoader fxmlLoader = new FXMLLoader(DataBaseApplication.class.getResource("HomersDataBaseAppView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 720);
        stage.setTitle("HomersDataBase");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}