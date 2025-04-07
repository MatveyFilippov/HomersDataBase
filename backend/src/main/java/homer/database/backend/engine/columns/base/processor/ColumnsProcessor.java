package homer.database.backend.engine.columns.base.processor;

import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.base.implementations.SimpleColumn;
import homer.database.backend.engine.columns.base.implementations.UniqueColumn;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.base.Parser;
import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import java.util.Arrays;

public class ColumnsProcessor {

    public static void createPrimaryColumn(String name, Class<? extends DataType<?>> dataTypeClass) {
        if (ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new HomerDataBaseUncheckedException("Primary column already exists");
        }
        DataType<?> dataType = Parser.getNullInstance(dataTypeClass);
        ColumnParamsHashTable.registerNewPrimaryColumn(name, dataType.getDataTypeName());
    }

    public static void newColumn(String name, Class<? extends DataType<?>> dataTypeClass, boolean isUnique, boolean isNullPossible) throws ColumnExistenceException {
        if (!ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new HomerDataBaseUncheckedException("Before creating new column, you must create primary column");
        }
        if (ColumnParamsHashTable.isColumnNameUsed(name)) {
            throw new ColumnExistenceException(name, true);
        }
        DataType<?> dataType = Parser.getNullInstance(dataTypeClass);
        ColumnParamsHashTable.registerNewColumn(name, dataType.getDataTypeName(), isUnique, isNullPossible);
    }

    public static void cleanColumn(String name) {
        try {
            ColumnParamsHashTable columnParamsToClean = new ColumnParamsHashTable(name);
            if (columnParamsToClean.isPrimaryColumn()) {
                throw new HomerDataBaseUncheckedException("You can't clean primary column");
            }
            Column<? extends DataType<?>> columnToClean = exportColumnParamsToColumn(columnParamsToClean);
            columnToClean.cleanColumn();
        } catch (ColumnExistenceException ignored) {}
    }

    public static void deleteColumn(String name) {
        try {
            ColumnParamsHashTable columnParamsToDel = new ColumnParamsHashTable(name);
            if (columnParamsToDel.isPrimaryColumn()) {
                throw new HomerDataBaseUncheckedException("You can't delete primary column");
            }
            Column<? extends DataType<?>> columnToDel = exportColumnParamsToColumn(columnParamsToDel);
            columnToDel.deleteColumn();
        } catch (ColumnExistenceException ignored) {}
        ColumnParamsHashTable.deleteColumn(name);
    }

    private static <DT extends DataType<?>> Column<DT> exportColumnParamsToColumn(ColumnParamsHashTable columnParams) {
        Class<DT> columnDataType = (Class<DT>) Parser.findDataTypeClass(columnParams.getDataType());
        if (columnParams.isOnlyUniqueValuesInColumn()) {
            return new UniqueColumn<>(columnParams.columnName, columnDataType);
        } else {
            return new SimpleColumn<>(columnParams.columnName, columnParams.isNullValuesPossible(), columnDataType);
        }
    }

    public static String[] getColumnNames() {
        return Arrays.stream(ColumnParamsHashTable.getAllColumns()).map(c -> c.columnName).toArray(String[]::new);
    }

    public static <DT extends DataType<?>> Column<DT> getColumn(String name) throws ColumnExistenceException {
        ColumnParamsHashTable columnParams = new ColumnParamsHashTable(name);
        return exportColumnParamsToColumn(columnParams);
    }

    public static <DT extends DataType<?>> UniqueColumn<DT> getPrimaryColumn() throws ColumnExistenceException {
        return (UniqueColumn<DT>) getColumn(ColumnParamsHashTable.getPrimaryColumnName());
    }

}
