package homer.database.gui.table;

import homer.database.backend.engine.datatypes.helpers.DataTypes;
import homer.database.gui.misc.ErrorAlert;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.HashMap;
import java.util.Map;

class Frontend {
    static TableView<ObservableList<String>> table;
    static final Map<String, Integer> columns = new HashMap<>();

    static void setItemsToNewColumnDataTypeChoiceBox(DataTypes[] values, ChoiceBox<DataTypes> choiceBox) {
        ObservableList<DataTypes> valuesToChoiceBox = FXCollections.observableArrayList(values);
        choiceBox.setItems(valuesToChoiceBox);
    }

    static void cleanTable() {
        columns.clear();
        table.getItems().clear();
        table.getColumns().clear();
    }

    static void addColumn(String columnHeader) {
        TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnHeader);
        final int colIndex = columns.size();

        column.setCellValueFactory(cellData -> {
            ObservableList<String> row = cellData.getValue();
            if (row.size() > colIndex) {
                return new ReadOnlyStringWrapper(row.get(colIndex));
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            try {
                TableProcessor.cellEditCommit(event);
            } catch (NameNotFoundException | IOException | KeyException e) {
                ErrorAlert.showAlert("Commit error", e.getMessage());
            }
        });

        columns.put(columnHeader, table.getColumns().size());
        table.getColumns().add(column);
    }

    static void deleteColumn(String columnHeader) {
        table.getColumns().remove(getColumnIndex(columnHeader));
        columns.remove(columnHeader);
    }

    static void makeSureThatLastLineIsFree() {
        int lastLineIndex = table.getItems().size() - 1;
        if (lastLineIndex == -1) {
            createRow();
        } else {
            ObservableList<String> lastRow = table.getItems().get(lastLineIndex);
            for (String element : lastRow) {
                if (element != null && !element.isEmpty()) {
                    createRow();
                    return;
                }
            }
        }
    }

    static void makeSureThatAllLinesContainsRightCellQTY() {
        final int cellQTY = columns.size();
        for (ObservableList<String> row : table.getItems()) {
            for (int i = row.size(); i < cellQTY; i++) {
                row.add("");
            }
            System.out.println();
        }
    }

    static int createRow() {
        ObservableList<String> row = FXCollections.observableArrayList();
        for (int i = 0; i < columns.size(); i++) {
            row.add("");
        }
        table.getItems().add(row);
        return table.getItems().size() - 1;
    }

    static void deleteRow(int rowIndex) {
        table.getItems().remove(rowIndex);
    }

    static int getColumnIndex(String columnHeader) {
        return columns.getOrDefault(columnHeader, -1);
    }

    static void putDataInCell(int row, int column, String data) {
        table.getItems().get(row).set(column, data == null ? "" : data);
    }

    static void putDataInCell(int row, String columnHeader, String data) {
        putDataInCell(row, getColumnIndex(columnHeader), data);
    }

    static String getCellValue(int row, int column) {
        return table.getItems().get(row).get(column);
    }

    static String getCellValue(int row, String columnHeader) {
        return getCellValue(row, getColumnIndex(columnHeader));
    }
}
