package homer.database.gui.misc;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AlertWindow {

    public static Alert create(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        if (header != null) {
            alert.setHeaderText(header);
        }
        alert.setContentText(content);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return alert;
    }

    private static void show(Alert alert, boolean wait) {
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    private static void showInFxApplicationThread(Alert alert, boolean wait) {
        if (Platform.isFxApplicationThread()) {
            show(alert, wait);
        } else {
            Platform.runLater(() -> show(alert, wait));
        }
    }

    public static void showError(String errorName, String errorText, boolean wait) {
        showInFxApplicationThread(create(Alert.AlertType.ERROR, "Error", errorName, errorText), wait);
    }

    public static void showInfo(String header, String content, boolean wait) {
        showInFxApplicationThread(create(Alert.AlertType.INFORMATION, "Info", header, content), wait);
    }

}
