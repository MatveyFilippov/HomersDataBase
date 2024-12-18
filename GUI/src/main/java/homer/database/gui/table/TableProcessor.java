package homer.database.gui.table;

import homer.database.backend.DataBase;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.helpers.DataType;
import homer.database.backend.engine.datatypes.helpers.DataTypes;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.List;

public class TableProcessor {
    private static String primaryColumnName;

    public static void init(TableView<ObservableList<String>> table, ChoiceBox<DataTypes> newColumnDataTypeChoiceBox,
                            ComboBox<Button> columnsToDelComboBox, ChoiceBox<String> columnNameToFindChoiceBox) throws NameNotFoundException, IOException {
        Frontend.table = table;
        Frontend.columnsToDelComboBox = columnsToDelComboBox;
        Frontend.columnNameToFindChoiceBox = columnNameToFindChoiceBox;
        Frontend.setItemsToNewColumnDataTypeChoiceBox(DataTypes.values(), newColumnDataTypeChoiceBox);
        refresh();
    }

    public static boolean isNoColumns() {
        return Frontend.columns.isEmpty();
    }

    public static void refresh() throws IOException, NameNotFoundException {
        Frontend.cleanTable();
        try {
            fillColumnsFromDB();
            fillRowsFromDB();
        } catch (KeyException ignored) {}
        Frontend.makeSureThatLastLineIsFree();
        Frontend.makeSureThatAllLinesContainsRightCellQTY();
    }

    private static void fillColumnsFromDB() throws IOException, NameNotFoundException, KeyException {
        primaryColumnName = DataBase.getPrimaryColumnName();
        Frontend.addColumn(DataBase.getColumnHeader(primaryColumnName));
        for (String columnName : DataBase.getColumnNames()) {
            if (!columnName.equals(primaryColumnName)) {
                Frontend.addColumn(DataBase.getColumnHeader(columnName));
            }
        }
    }

    private static void fillRowsFromDB() throws IOException, NameNotFoundException, KeyException {
        for (RecordUniqueID lineID : DataBase.getAllRecordsIds()) {
            int rowIndex = Frontend.createRow();
            for (String columnName : DataBase.getColumnNames()) {
                DataType data = DataBase.readValue(columnName, lineID);
                Frontend.putDataInCell(
                        rowIndex, DataBase.getColumnHeader(columnName),
                        data == null ? "" : data.toString()
                );
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

    private static void writeValueInDataBase(String columnName, String primaryKey, DataType newValue) throws NameNotFoundException, IOException, KeyException {
        if (columnName.equals(primaryColumnName)) {
            DataBase.createNewLine(newValue);
        } else {
            RecordUniqueID lineID = new RecordUniqueID(
                    DataBase.getColumnDataType(primaryColumnName).parseValue(primaryKey)
            );
            DataBase.writeValue(columnName, lineID, newValue);
        }
    }

    private static void deleteValueInDataBase(String columnName, String primaryKey) throws NameNotFoundException, IOException, KeyException {
        RecordUniqueID lineID = new RecordUniqueID(
                DataBase.getColumnDataType(primaryColumnName).parseValue(primaryKey)
        );
        DataBase.deleteValue(columnName, lineID);
    }

    private static String editValueInDataBase(String columnName, String primaryKey, String value) throws NameNotFoundException, IOException, KeyException {
        DataType newValue = DataBase.getColumnDataType(columnName).parseValue(value);
        if (newValue == null || newValue.toString().equals("null")) {
            if (value != null && !value.isEmpty()) {
                throw new IOException("Invalid value for this column (error with DataType)");
            }
            deleteValueInDataBase(columnName, primaryKey);
        } else {
            writeValueInDataBase(columnName, primaryKey, newValue);
        }
        return newValue == null ? null : newValue.toString();
    }

    static void cellEditCommit(TableColumn.CellEditEvent<ObservableList<String>, String> event) throws NameNotFoundException, IOException, KeyException {
        TablePosition<ObservableList<String>, String> position = event.getTablePosition();
        int rowIndex = position.getRow();
        String columnName = removeColumnDataTypeFromColumnHeader(position.getTableColumn().getText());
        String newValue = event.getNewValue();
        String primaryKey = "";
        if (!columnName.equals(primaryColumnName)) {
            primaryKey = Frontend.getCellValue(rowIndex, 0);
            if (primaryKey == null || primaryKey.isEmpty()) {
                throw new IOException("Before set value you should set primary key");
            }
        }
        String parsedValue = editValueInDataBase(columnName, primaryKey, newValue);
        Frontend.putDataInCell(rowIndex, position.getColumn(), parsedValue);
        Frontend.makeSureThatLastLineIsFree();
    }

    public static void createPrimaryColumn(String columnName, DataTypes columnDataType) throws IOException, NameNotFoundException {
        DataBase.createTable(columnName, columnDataType);
        Frontend.addColumn(DataBase.getColumnHeader(columnName));
        primaryColumnName = columnName;
        Frontend.makeSureThatLastLineIsFree();
        Frontend.makeSureThatAllLinesContainsRightCellQTY();
    }

    public static void createColumn(String columnName, DataTypes columnDataType, boolean isUnique, boolean isNullPossible) throws IOException, NameNotFoundException {
        DataBase.createColumn(columnName, columnDataType, isUnique, isNullPossible);
        Frontend.addColumn(DataBase.getColumnHeader(columnName));
        Frontend.makeSureThatLastLineIsFree();
        Frontend.makeSureThatAllLinesContainsRightCellQTY();
    }

    public static void deleteColumn(String columnName) throws IOException, NameNotFoundException {
        columnName = removeColumnDataTypeFromColumnHeader(columnName);
        if (columnName.equals(primaryColumnName)) {
            deleteTable();
        } else {
            String columnHeader = DataBase.getColumnHeader(columnName);
            DataBase.deleteColumn(columnName);
            Frontend.deleteColumn(columnHeader);
        }
    }

    public static void deleteTable() {
        DataBase.deleteTable();
        Frontend.cleanTable();
    }

    public static void findAllValues(String columnNameToFind, String value) throws NameNotFoundException, IOException {
        columnNameToFind = removeColumnDataTypeFromColumnHeader(columnNameToFind);
        List<RecordUniqueID> lineIDs = DataBase.findValues(columnNameToFind, DataBase.tryToParseValue(columnNameToFind, value));
        Frontend.cleanTable();
        try {
            fillColumnsFromDB();
            for (RecordUniqueID lineID : lineIDs) {
                int rowIndex = Frontend.createRow();
                for (String columnName : DataBase.getColumnNames()) {
                    DataType data = DataBase.readValue(columnName, lineID);
                    Frontend.putDataInCell(
                            rowIndex, DataBase.getColumnHeader(columnName),
                            data == null ? "" : data.toString()
                    );
                }
            }
        } catch (KeyException ignored) {}
        Frontend.makeSureThatLastLineIsFree();
        Frontend.makeSureThatAllLinesContainsRightCellQTY();
    }
}
