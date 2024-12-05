package homer.database.backend;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.UniqueColumn;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.helpers.DataType;
import homer.database.backend.engine.columns.helpers.ColumnsProcessor;
import homer.database.backend.engine.datatypes.helpers.DataTypes;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    public static void setPathToDataBase(String ... pathsToTable) {
        FileProcessor.pathToDataBaseRootDir = FileProcessor.getAbsolute(FileProcessor.join(pathsToTable));
    }

    public static void deleteTable() {
        FileProcessor.deleteDir(FileProcessor.pathToDataBaseRootDir);
        FileProcessor.pathToDataBaseRootDir = null;
    }

    public static void cleanTable() {
        FileProcessor.cleanDir(FileProcessor.join(
                FileProcessor.pathToDataBaseRootDir, FileProcessor.Constants.HDBC_FOLDER_NAME
        ));
        FileProcessor.cleanDir(FileProcessor.join(
                FileProcessor.pathToDataBaseRootDir, FileProcessor.Constants.HDBT_FOLDER_NAME
        ));
    }

    public static void createTable(String primaryColumnName, DataTypes primaryColumnDataType) throws IOException {
        ColumnsProcessor.createPrimaryColumn(primaryColumnName, primaryColumnDataType);
    }

    public static void createColumn(String columnName, DataTypes columnDataType, boolean isUnique, boolean isNullPossible) throws IOException {
        ColumnsProcessor.newColumn(columnName, columnDataType, isUnique, isNullPossible);
    }

    public static void deleteColumn(String columnName) throws IOException {
        ColumnsProcessor.deleteColumn(columnName);
    }

    public static String getPrimaryColumnName() throws IOException, NameNotFoundException, KeyException {
        UniqueColumn<? extends DataType> column = ColumnsProcessor.getPrimaryColumn();
        return column.columnName;
    }

    public static List<String> getColumnNames() throws IOException {
        List<String> columnNames = new ArrayList<>();
        for (Column<? extends DataType> column : ColumnsProcessor.getColumns()) {
            columnNames.add(column.columnName);
        }
        return columnNames;
    }

    public static String getColumnHeader(String columnName) throws IOException, NameNotFoundException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        return column.toString();
    }

    public static DataTypes getColumnDataType(String columnName) throws NameNotFoundException, IOException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        return column.dataType;
    }

    public static <DT extends DataType> DT tryToParseValue(String columnName, String value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.dataType.parseValue(value);
    }

    public static <DT extends DataType> RecordUniqueID createNewLine(DT primaryKey) throws NameNotFoundException, IOException, KeyException {
        UniqueColumn<DT> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        RecordUniqueID key = new RecordUniqueID(primaryKey);
        primaryColumn.writeValue(key, key.toDataType());
        return key;
    }

    public static List<RecordUniqueID> getAllRecordsIds() throws NameNotFoundException, IOException, KeyException {
        List<RecordUniqueID> values = new ArrayList<>();
        UniqueColumn<? extends DataType> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        for (DataType value : primaryColumn.getAllValues()) {
            values.add(new RecordUniqueID(value));
        }
        return values;
    }

    public static <DT extends DataType> void writeValue(String columnName, RecordUniqueID recordUniqueID, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        column.writeValue(recordUniqueID, value);
    }

    public static <DT extends DataType> DT readValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.readValue(recordUniqueID);
    }

    public static <DT extends DataType> List<RecordUniqueID> findValues(String columnName, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.getRecordsUniqueID(value);
    }

    private static void deleteValueWithoutCheckingToPrimary(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        column.deleteValue(recordUniqueID);
    }

    public static void deleteValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException, KeyException {
        if (columnName.equals(ColumnsProcessor.getPrimaryColumn().columnName)) {
            deleteLine(recordUniqueID);
        } else {
            deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
        }
    }

    public static <DT extends DataType> void deleteValues(String columnName, DT value) throws NameNotFoundException, IOException, KeyException {
        if (columnName.equals(ColumnsProcessor.getPrimaryColumn().columnName)) {
            deleteLine(findValues(columnName, value).get(0));
        } else {
            for (RecordUniqueID recordUniqueID : findValues(columnName, value)) {
                deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
            }
        }
    }

    public static void deleteLine(RecordUniqueID recordUniqueID) throws IOException, NameNotFoundException {
        for (Column<? extends DataType> column : ColumnsProcessor.getColumns()) {
            deleteValueWithoutCheckingToPrimary(column.columnName, recordUniqueID);
        }
    }
}
