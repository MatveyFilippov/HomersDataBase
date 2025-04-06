package homer.database.backend;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.UniqueColumn;
import homer.database.backend.engine.columns.base.ColumnsProcessor;
import homer.database.backend.engine.columns.base.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.Parser;
import homer.database.backend.engine.datatypes.implementations.BoolType;
import homer.database.backend.engine.datatypes.implementations.NumberType;
import homer.database.backend.engine.datatypes.implementations.StringType;
import homer.database.backend.engine.datatypes.implementations.TimeType;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    static {
        Parser.registerDataTypeClass(StringType.class);
        Parser.registerDataTypeClass(NumberType.class);
        Parser.registerDataTypeClass(BoolType.class);
        Parser.registerDataTypeClass(TimeType.class);
    }

    public static void registerNewDataTypeClass(Class<? extends DataType<?>> dataType) {
        Parser.registerDataTypeClass(dataType);
    }

    public static void openTable(Path pathToTable) {
        FileProcessor.set(pathToTable);
    }

    public static void openTable(String... pathsToTable) {
        FileProcessor.set(FileProcessor.toAbsolutePath(pathsToTable));
    }

    public static void createTable(String primaryColumnName, Class<? extends DataType<?>> primaryColumnDataType) throws IOException {
        ColumnsProcessor.createPrimaryColumn(primaryColumnName, primaryColumnDataType);
    }

    public static void deleteTable() {
        FileProcessor.deleteDir(FileProcessor.getPathToDataBaseRootDir());
        FileProcessor.unset();
    }

    public static void cleanTable() {
        FileProcessor.cleanDir(FileProcessor.toAbsolutePathFromRoot(FileProcessor.Constants.HDBC_FOLDER_NAME));
        FileProcessor.cleanDir(FileProcessor.toAbsolutePathFromRoot(FileProcessor.Constants.HDBT_FOLDER_NAME));
    }

    public static void createColumn(String columnName, Class<? extends DataType<?>> columnDataType, boolean isUnique, boolean isNullPossible) throws IOException {
        ColumnsProcessor.newColumn(columnName, columnDataType, isUnique, isNullPossible);
    }

    public static void cleanColumn(String columnName) throws IOException {
        ColumnsProcessor.cleanColumn(columnName);
    }

    public static void deleteColumn(String columnName) throws IOException {
        ColumnsProcessor.deleteColumn(columnName);
    }

    public static String getPrimaryColumnName() throws IOException, NameNotFoundException, KeyException {
        UniqueColumn<? extends DataType<?>> column = ColumnsProcessor.getPrimaryColumn();
        return column.getColumnName();
    }

    public static List<String> getColumnNames() throws IOException {
        List<String> columnNames = new ArrayList<>();
        for (Column<? extends DataType<?>> column : ColumnsProcessor.getColumns()) {
            columnNames.add(column.getColumnName());
        }
        return columnNames;
    }

    private static boolean isPrimaryColumn(String columnName) throws NameNotFoundException, IOException, KeyException {
        return columnName.equals(getPrimaryColumnName());
    }

    public static String getColumnHeader(String columnName) throws IOException, NameNotFoundException {
        Column<? extends DataType<?>> column = ColumnsProcessor.getColumn(columnName);
        return column.getDataBaseHeader();
    }

    public static Class<? extends DataType<?>> getColumnDataType(String columnName) throws NameNotFoundException, IOException {
        Column<? extends DataType<?>> column = ColumnsProcessor.getColumn(columnName);
        return column.getColumnDataTypeClass();
    }

    public static <DT extends DataType<?>> DT tryToParseValue(String columnName, String value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return Parser.getInstance(column.getColumnDataTypeClass(), value);
    }

    public static <DT extends DataType<?>> RecordUniqueID createNewLine(DT primaryKey) throws NameNotFoundException, IOException, KeyException {
        UniqueColumn<DT> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        RecordUniqueID key = new RecordUniqueID(primaryKey);
        primaryColumn.writeValue(key, key.toDataType());
        return key;
    }

    public static List<RecordUniqueID> getAllRecordsIds() throws NameNotFoundException, IOException, KeyException {
        List<RecordUniqueID> values = new ArrayList<>();
        UniqueColumn<? extends DataType<?>> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        for (DataType<?> value : primaryColumn.getAllValues()) {
            values.add(new RecordUniqueID(value));
        }
        return values;
    }

    public static <DT extends DataType<?>> void writeValue(String columnName, RecordUniqueID recordUniqueID, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        column.writeValue(recordUniqueID, value);
    }

    public static <DT extends DataType<?>> DT readValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.readValue(recordUniqueID);
    }

    public static <DT extends DataType<?>> List<RecordUniqueID> findValues(String columnName, DT value) throws NameNotFoundException, IOException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.getRecordsUniqueID(value);
    }

    private static void deleteValueWithoutCheckingToPrimary(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException {
        Column<? extends DataType<?>> column = ColumnsProcessor.getColumn(columnName);
        if (!column.isNullValuesPossible()) {
            throw new IOException("You can't delete value in column where values can't be null");
        }
        column.deleteValue(recordUniqueID);
    }

    public static void deleteValue(String columnName, RecordUniqueID recordUniqueID) throws NameNotFoundException, IOException, KeyException {
        if (isPrimaryColumn(columnName)) {
            deleteLine(recordUniqueID);
        } else {
            deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
        }
    }

    public static <DT extends DataType<?>> void deleteValues(String columnName, DT value) throws NameNotFoundException, IOException, KeyException {
        if (isPrimaryColumn(columnName)) {
            deleteLine(findValues(columnName, value).get(0));
        } else {
            for (RecordUniqueID recordUniqueID : findValues(columnName, value)) {
                deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
            }
        }
    }

    public static void deleteLine(RecordUniqueID recordUniqueID) throws IOException {
        for (Column<? extends DataType<?>> column : ColumnsProcessor.getColumns()) {
            column.deleteValue(recordUniqueID);
        }
    }

}
