package homer.database.gui.misc;

import javafx.scene.control.Alert;

public class ErrorAlert {
    public static Alert createAlert(String errorName, String errorText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(errorName);
        alert.setContentText(errorText);
        return alert;
    }

    public static void showAlert(String errorName, String errorText, boolean wait) {
        Alert alert = createAlert(errorName, errorText);
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    public static void showAlert(String errorName, String errorText) {
        showAlert(errorName, errorText, true);
    }
}
