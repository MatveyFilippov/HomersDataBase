package homer.database.gui;

import homer.database.gui.misc.ErrorHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HomerDataBaseApplication extends Application {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        HomerDataBaseApplication.stage = stage;
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler::appErrorHandler);

        FXMLLoader fxmlLoader = new FXMLLoader(HomerDataBaseApplication.class.getResource("HomerDataBaseAppView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 720);
        stage.setTitle("HomerDataBase");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}