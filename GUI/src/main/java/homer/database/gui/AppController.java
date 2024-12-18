package homer.database.gui;

import homer.database.backend.DataBase;
import homer.database.backend.engine.datatypes.helpers.DataTypes;
import homer.database.gui.table.TableProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import org.checkerframework.checker.units.qual.A;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;

public class AppController {

    @FXML
    ComboBox<Button> columnsToDelComboBox;

    @FXML
    CheckBox isNewColumnNullPossible, isNewColumnUnique;

    @FXML
    ChoiceBox<DataTypes> newColumnDataTypeChoiceBox;

    @FXML
    ChoiceBox<String> columnNameToFindChoiceBox;

    @FXML
    TextField newColumnNameField, valueToFindField;

    @FXML
    TableView<ObservableList<String>> table;

    @FXML
    private void initialize() throws NameNotFoundException, IOException {
        DataBase.setPathToDataBase(AppProperties.PATH_TO_DATA_DIR, AppProperties.DB_NAME);
        TableProcessor.init(table, newColumnDataTypeChoiceBox, columnsToDelComboBox, columnNameToFindChoiceBox);
        resetToDefaultNewColumnCreationLine();
        setToWaitingNewPrimaryColumnCreationLineIfNecessary();
    }

    @FXML
    void createNewColumn() throws NameNotFoundException, IOException {
        String columnName = newColumnNameField.getText();
        DataTypes columnDataType = newColumnDataTypeChoiceBox.getValue();
        boolean isColumnUnique = isNewColumnUnique.isSelected();
        boolean isColumnNullPossible = isNewColumnNullPossible.isSelected();
        if (TableProcessor.isNoColumns()) {
            TableProcessor.createPrimaryColumn(columnName, columnDataType);
        } else {
            TableProcessor.createColumn(columnName, columnDataType, isColumnUnique, isColumnNullPossible);
        }
        resetToDefaultNewColumnCreationLine();
        setToWaitingNewPrimaryColumnCreationLineIfNecessary();
    }

    private void resetToDefaultNewColumnCreationLine() {
        newColumnNameField.setText("");
        newColumnDataTypeChoiceBox.setValue(DataTypes.STRING);
    }

    private void setToWaitingNewPrimaryColumnCreationLineIfNecessary() {
        boolean isWaitingNewPrimaryColumn = TableProcessor.isNoColumns();
        isNewColumnUnique.setDisable(isWaitingNewPrimaryColumn);
        isNewColumnNullPossible.setDisable(isWaitingNewPrimaryColumn);
        newColumnNameField.setPromptText(
                isWaitingNewPrimaryColumn ? "PrimaryColumn name" : "New column name"
        );
    }

    @FXML
    void tryToFindValue() throws NameNotFoundException, IOException {
        TableProcessor.findAllValues(columnNameToFindChoiceBox.getValue(), valueToFindField.getText());
    }

    @FXML
    void refreshTable() throws NameNotFoundException, IOException {
        TableProcessor.refresh();
    }

    @FXML
    void exportToBACKUP() throws IOException {
        homer.database.converter.backup.Exporter.toBackupFile();
    }

    @FXML
    void importFromBACKUP() throws IOException, NameNotFoundException {
        homer.database.converter.backup.Importer.fromBackupFile(
                "/Users/matvey/IdeaProjects/HomersDataBase/GUI/HomersDataBaseAppData/AppMainDataBase.HDBB",
                AppProperties.PATH_TO_DATA_DIR
        );
        TableProcessor.refresh();
    }

    @FXML
    void exportToCSV() throws NameNotFoundException, IOException, KeyException {
        homer.database.converter.csv.Exporter.toCSV();
    }
}