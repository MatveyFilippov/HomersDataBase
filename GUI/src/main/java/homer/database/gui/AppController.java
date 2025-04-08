package homer.database.gui;

import homer.database.backend.HomerDataBase;
import homer.database.converter.Extension;
import homer.database.gui.misc.AlertWindow;
import homer.database.gui.misc.DialogWindow;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AppController {

    @FXML
    TableView<ObservableList<String>> table;

    @FXML
    private void initialize() {
        HomerDataBase.open(AppProperties.PATH_TO_DATABASE);
    }

    @FXML
    void exportToBACKUP() throws IOException {
        if (!HomerDataBase.isTableCreated()) {
            AlertWindow.showError("Can't export HomerDataBse", "Table is clear", true);
            return;
        }
        File exportFile = DialogWindow.fileChooserToSave("Choose file to save export", Map.of(
                "HomerDataBaseBackup", List.of("*" + Extension.HDBB)
        ));
        if (exportFile == null) {
            AlertWindow.showError("Can't export file", "You don't choose anything", true);
            return;
        }
        homer.database.converter.backup.Exporter.toBackup(exportFile);
        AlertWindow.showInfo("Success", "HomerDataBase exported to backup file: " + exportFile, false);
    }

    @FXML
    void importFromBACKUP() throws IOException {
        File exportFile = DialogWindow.fileChooserToOpen("Choose export file", Map.of(
                "HomerDataBaseBackup", List.of("*" + Extension.HDBB),
                "All files", List.of("*.*")
        ));
        if (exportFile == null) {
            AlertWindow.showError("Can't open export file", "You don't choose anything", true);
            return;
        }
        boolean isContinueOpening = !HomerDataBase.isTableCreated() || DialogWindow.askBool(
                "Continue importing", "Stop", "Backup import",
                "After importing, the previous data will be deleted",
                "If you want to save your data, export it to backup"
        );
        if (!isContinueOpening) {
            return;
        }
        homer.database.converter.backup.Importer.fromBackup(exportFile, AppProperties.PATH_TO_DATABASE);
        AlertWindow.showInfo("Success", "HomerDataBase imported from backup file", false);
    }

    @FXML
    void exportToCSV() throws IOException {
        if (!HomerDataBase.isTableCreated()) {
            AlertWindow.showError("Can't export HomerDataBse", "Table is clear", true);
            return;
        }
        File exportFile = DialogWindow.fileChooserToSave("Choose file to save CSV export", Map.of(
                "CommaSeparatedValues", List.of("*" + Extension.CSV)
        ));
        if (exportFile == null) {
            AlertWindow.showError("Can't export file", "You don't choose anything", true);
            return;
        }
        homer.database.converter.csv.Exporter.toCSV(exportFile);
        AlertWindow.showInfo("Success", "HomerDataBase exported to csv file: " + exportFile, false);
    }

}
