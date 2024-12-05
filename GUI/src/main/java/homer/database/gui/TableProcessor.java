package homer.database.gui;

import homer.database.backend.DataBase;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.helpers.DataType;
import homer.database.backend.engine.datatypes.helpers.DataTypes;
import homer.database.gui.misc.ErrorAlert;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;

public class TableProcessor {
    private static TableView<ObservableList<String>> table;
    private static int columnsQTY = 0;
    private static String primaryColumnName;

    public static void init(TableView<ObservableList<String>> table) throws NameNotFoundException, IOException {
        TableProcessor.table = table;
        refresh();
    }


    public static void refresh() throws IOException, NameNotFoundException {
        cleanTable();
        try {
            setColumnsFromDB();
            setRowsFromDB();
        } catch (KeyException ignored) {}
        createRow();
    }

    private static void cleanTable() {
        columnsQTY = 0;
        table.getItems().clear();
        table.getColumns().clear();

    }

    private static void setColumnsFromDB() throws IOException, NameNotFoundException, KeyException {
        primaryColumnName = DataBase.getPrimaryColumnName();
        addColumn(DataBase.getColumnHeader(primaryColumnName));
        for (String columnName : DataBase.getColumnNames()) {
            columnsQTY += 1;
            if (!columnName.equals(primaryColumnName)) {
                addColumn(DataBase.getColumnHeader(columnName));
            }
        }
    }

    private static void setRowsFromDB() throws IOException, NameNotFoundException, KeyException {
        for (RecordUniqueID lineID : DataBase.getAllRecordsIds()) {
            int rowIndex = createRow();
            putDataInCell(rowIndex, 0, DataBase.readValue(primaryColumnName, lineID).toString());
            int columnIndex = 1;
            for (String columnName : DataBase.getColumnNames()) {
                if (!columnName.equals(primaryColumnName)) {
                    putDataInCell(rowIndex, columnIndex, DataBase.readValue(columnName, lineID).toString());
                    columnIndex += 1;
                }
            }
        }
    }

    private static String removeColumnDataTypeFromColumnHeader(String columnHeader) {
        String columnName = columnHeader;
        for (DataTypes dataType : DataTypes.values()) {
            String toReplace = " (" + dataType + ")";
            columnName = columnName.replace(toReplace, "");
        }
        return columnName;
    }

    private static void cellEditCommit(TableColumn.CellEditEvent<ObservableList<String>, String> event) throws NameNotFoundException, IOException, KeyException {
        TablePosition<ObservableList<String>, String> position = event.getTablePosition();
        String columnName = removeColumnDataTypeFromColumnHeader(position.getTableColumn().getText());
        DataType newValue = DataBase.getColumnDataType(columnName).parseValue(event.getNewValue());
        if (newValue == null && !(event.getNewValue() == null || event.getNewValue().isEmpty())) {
            throw new IOException("Invalid value for this DataType");
        }
        if (columnName.equals(primaryColumnName)) {
            DataBase.createNewLine(newValue);
        } else {
            String primaryKey = table.getItems().get(position.getRow()).get(0);
            if (primaryKey == null || primaryKey.isEmpty()) {
                throw new IOException("Before set value you should set primary key");
            }
            RecordUniqueID lineID = new RecordUniqueID(
                    DataBase.getColumnDataType(primaryColumnName).parseValue(primaryKey)
            );
            DataBase.writeValue(columnName, lineID, newValue);
        }
        putDataInCell(
                position.getRow(), position.getColumn(),
                newValue == null ? "" : newValue.toString()
        );
    }

    private static void addColumn(String columnName) {
        TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
        final int colIndex = table.getColumns().size();

        column.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(colIndex)));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            try {
                cellEditCommit(event);
            } catch (NameNotFoundException | IOException | KeyException e) {
                ErrorAlert.showAlert("Commit error", e.getMessage());
            }
        });

        table.getColumns().add(column);
        columnsQTY += 1;
    }

    private static int createRow() {
        ObservableList<String> row = FXCollections.observableArrayList();
        for (int i = 0; i < columnsQTY; i++) {
            row.add("");
        }
        table.getItems().add(row);
        return table.getItems().size() - 1;
    }

    private static void putDataInCell(int row, int column, String data) {
        table.getItems().get(row).add(column, data);
    }

    public static void createColumn(String columnName, DataTypes columnDataType, boolean isUnique, boolean isNullPossible) throws IOException, NameNotFoundException {
        DataBase.createColumn(columnName, columnDataType, isUnique, isNullPossible);
        addColumn(columnName);
    }
}
