package homer.database;

import homer.database.engine.FileProcessor;
import homer.database.engine.columns.Column;
import homer.database.engine.columns.UniqueColumn;
import homer.database.engine.columns.helpers.RecordUniqueID;
import homer.database.engine.datatypes.helpers.DataType;
import homer.database.engine.columns.helpers.ColumnsProcessor;
import homer.database.engine.datatypes.helpers.DataTypes;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    public DataBase(String rootDataBaseDirPath, String tableName) {
        FileProcessor.pathToDataBaseRootDir = FileProcessor.join(rootDataBaseDirPath, tableName);
    }

    public void deleteTable() {
        FileProcessor.deleteRootDir();
    }

    public void createTable(String primaryColumnName, DataTypes primaryColumnDataType) throws IOException {
        ColumnsProcessor.createPrimaryColumn(primaryColumnName, primaryColumnDataType);
    }

    public void createColumn(String columnName, DataTypes columnDataType, boolean isUnique, boolean isNullPossible) throws IOException {
        ColumnsProcessor.newColumn(columnName, columnDataType, isUnique, isNullPossible);
    }

    public void deleteColumn(String columnName) throws IOException {
        ColumnsProcessor.deleteColumn(columnName);
    }

    public String getPrimaryColumnName() throws IOException, NameNotFoundException, KeyException {
        UniqueColumn<? extends DataType> column = ColumnsProcessor.getPrimaryColumn();
        return column.columnName;
    }

    public List<String> getColumnNames() throws IOException {
        List<String> columnNames = new ArrayList<>();
        for (Column<? extends DataType> column : ColumnsProcessor.getColumns()) {
            columnNames.add(column.columnName);
        }
        return columnNames;
    }

    public String getColumnHeader(String columnName) throws IOException, NameNotFoundException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        return column.toString();
    }

    public DataTypes getColumnDataType(String columnName) throws NameNotFoundException, IOException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        return column.dataType;
    }

    public <DT extends DataType> DT tryToParseValue(String columnName, String value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.dataType.parseValue(value);
    }

    public <DT extends DataType> RecordUniqueID createNewLine(DT primaryKey) throws NameNotFoundException, IOException, KeyException {
        UniqueColumn<DT> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        RecordUniqueID key = new RecordUniqueID(primaryKey);
        primaryColumn.writeValue(key, key.toDataType());
        return key;
    }

    public List<RecordUniqueID> getAllRecordsIds() throws NameNotFoundException, IOException, KeyException {
        List<RecordUniqueID> values = new ArrayList<>();
        UniqueColumn<? extends DataType> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        for (DataType value : primaryColumn.getAllValues()) {
            values.add(new RecordUniqueID(value));
        }
        return values;
    }

    public <DT extends DataType> void writeValue(String columnName, RecordUniqueID recordUniqueID, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        column.writeValue(recordUniqueID, value);
    }

    public <DT extends DataType> DT readValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.readValue(recordUniqueID);
    }

    public static <DT extends DataType> List<RecordUniqueID> findValues(String columnName, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.getRecordsUniqueID(value);
    }

    public static void deleteValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<? extends DataType> column = ColumnsProcessor.getColumn(columnName);
        column.deleteValue(recordUniqueID);
    }

    public static <DT extends DataType> void deleteValues(String columnName, DT value) throws NameNotFoundException, IOException {
        for (RecordUniqueID recordUniqueID : findValues(columnName, value)) {
            deleteValue(columnName, recordUniqueID);
        }
    }

    public static void deleteLine(RecordUniqueID recordUniqueID) throws IOException, NameNotFoundException {
        for (Column<? extends DataType> column : ColumnsProcessor.getColumns()) {
            deleteValue(column.columnName, recordUniqueID);
        }
    }
}
