package homer.database.backend;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.columns.base.implementations.UniqueColumn;
import homer.database.backend.engine.columns.base.processor.ColumnsProcessor;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.base.Parser;
import homer.database.backend.engine.datatypes.base.implementations.BoolType;
import homer.database.backend.engine.datatypes.base.implementations.NumberType;
import homer.database.backend.engine.datatypes.base.implementations.StringType;
import homer.database.backend.engine.datatypes.base.implementations.TimeType;
import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;
import java.nio.file.Path;
import java.util.Arrays;

public class HomerDataBase {

    static {
        registerNewDataTypeClass(StringType.class);
        registerNewDataTypeClass(NumberType.class);
        registerNewDataTypeClass(BoolType.class);
        registerNewDataTypeClass(TimeType.class);
    }

    public static void registerNewDataTypeClass(Class<? extends DataType<?>> dataType) {
        Parser.registerDataTypeClass(dataType);
    }

    public static boolean isOpen() {
        return FileProcessor.getPathToDataBaseRootDir() != null;
    }

    public static void open(Path pathToTable) {
        FileProcessor.set(pathToTable);
    }

    public static void open(String... pathsToTable) {
        open(FileProcessor.toAbsolutePath(pathsToTable));
    }

    public static void close() {
        FileProcessor.unset();
    }

    public static Path getOpened() {
        return FileProcessor.getPathToDataBaseRootDir();
    }

    public static boolean isTableCreated() {
        return ColumnsProcessor.isPrimaryColumnExists();
    }

    public static void createTable(String primaryColumnName, Class<? extends DataType<?>> primaryColumnDataType) {
        ColumnsProcessor.createPrimaryColumn(primaryColumnName, primaryColumnDataType);
    }

    public static void deleteTable() {
        FileProcessor.deleteDir(FileProcessor.getPathToDataBaseRootDir());
    }

    public static void cleanTable() {
        FileProcessor.cleanDir(FileProcessor.toAbsolutePathFromRoot(FileProcessor.Constants.HDBC_FOLDER_NAME));
        FileProcessor.cleanDir(FileProcessor.toAbsolutePathFromRoot(FileProcessor.Constants.HDBT_FOLDER_NAME));
    }

    public static boolean isColumnExists(String columnName) {
        return ColumnsProcessor.isColumnNameUsed(columnName);
    }

    public static void createColumn(String columnName, Class<? extends DataType<?>> columnDataType, boolean isUnique, boolean isNullPossible) throws ColumnExistenceException {
        ColumnsProcessor.newColumn(columnName, columnDataType, isUnique, isNullPossible);
    }

    public static void cleanColumn(String columnName) {
        ColumnsProcessor.cleanColumn(columnName);
    }

    public static void deleteColumn(String columnName) {
        ColumnsProcessor.deleteColumn(columnName);
    }

    public static String getPrimaryColumnName() throws ColumnExistenceException {
        UniqueColumn<? extends DataType<?>> column = ColumnsProcessor.getPrimaryColumn();
        return column.getColumnName();
    }

    public static String[] getColumnNames() {
        return ColumnsProcessor.getColumnNames();
    }

    private static boolean isPrimaryColumn(String columnName) {
        try {
            return columnName.equals(getPrimaryColumnName());
        } catch (ColumnExistenceException ignored) {
            return false;
        }
    }

    public static String getColumnHeader(String columnName) throws ColumnExistenceException {
        return ColumnsProcessor.getColumn(columnName).getDataBaseHeader();
    }

    public static Class<? extends DataType<?>> getColumnDataType(String columnName) throws ColumnExistenceException {
        return ColumnsProcessor.getColumn(columnName).getColumnDataTypeClass();
    }

    public static <DT extends DataType<?>> DT tryToParseValue(String columnName, String value) throws ColumnExistenceException, InvalidValueException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return Parser.getInstance(column.getColumnDataTypeClass(), value);
    }

    public static <DT extends DataType<?>> RecordUniqueID createNewLine(DT primaryKey) throws ColumnExistenceException, ReadWriteValueException {
        UniqueColumn<DT> primaryColumn = ColumnsProcessor.getPrimaryColumn();
        RecordUniqueID key = new RecordUniqueID(primaryKey);
        primaryColumn.writeValue(key, key.toDataType());
        return key;
    }

    public static RecordUniqueID[] getAllRecordIDs() throws ColumnExistenceException, ReadWriteValueException {
        return ColumnsProcessor.getPrimaryColumn().getRecordUniqueIDs();
    }

    public static <DT extends DataType<?>> void writeValue(String columnName, RecordUniqueID recordUniqueID, DT value) throws ColumnExistenceException, ReadWriteValueException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        column.writeValue(recordUniqueID, value);
    }

    public static <DT extends DataType<?>> DT readValue(String columnName, RecordUniqueID recordUniqueID) throws ColumnExistenceException, ReadWriteValueException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.readValue(recordUniqueID);
    }

    public static <DT extends DataType<?>> boolean isExists(String columnName, RecordUniqueID recordUniqueID) throws ColumnExistenceException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.isExists(recordUniqueID);
    }

    public static <DT extends DataType<?>> RecordUniqueID[] findValues(String columnName, DT value) throws ColumnExistenceException, ReadWriteValueException {
        Column<DT> column = ColumnsProcessor.getColumn(columnName);
        return column.find(value);
    }

    private static void deleteValueWithoutCheckingToPrimary(String columnName, RecordUniqueID recordUniqueID) throws ColumnExistenceException {
        Column<? extends DataType<?>> column = ColumnsProcessor.getColumn(columnName);
        if (!column.isNullValuesPossible()) {
            throw new HomerDataBaseUncheckedException("You can't delete value in column where values can't be null");
        }
        column.deleteValue(recordUniqueID);
    }

    public static void deleteValue(String columnName, RecordUniqueID recordUniqueID) throws ColumnExistenceException {
        if (isPrimaryColumn(columnName)) {
            deleteLine(recordUniqueID);
        } else {
            deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
        }
    }

    public static <DT extends DataType<?>> void deleteValues(String columnName, DT value) throws ColumnExistenceException, ReadWriteValueException {
        if (isPrimaryColumn(columnName)) {
            deleteLine(findValues(columnName, value)[0]);
        } else {
            for (RecordUniqueID recordUniqueID : findValues(columnName, value)) {
                deleteValueWithoutCheckingToPrimary(columnName, recordUniqueID);
            }
        }
    }

    public static void deleteLine(RecordUniqueID recordUniqueID) {
        Arrays.stream(ColumnsProcessor.getColumnNames()).forEach(column -> {
            try {
                ColumnsProcessor.getColumn(column).deleteValue(recordUniqueID);
            } catch (ColumnExistenceException ignored) {}
        });
    }

}
