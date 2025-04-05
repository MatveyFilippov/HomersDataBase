package homer.database.backend.engine.columns.base;

import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.SimpleColumn;
import homer.database.backend.engine.columns.UniqueColumn;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.Parser;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class ColumnsProcessor {

    public static void createPrimaryColumn(String name, Class<? extends DataType<?>> dataTypeClass) throws IOException {
        if (ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new KeyAlreadyExistsException("Primary column already exists");
        }
        DataType<?> dataType = Parser.getNullInstance(dataTypeClass);
        ColumnParamsHashTable.registerNewPrimaryColumn(name, dataType.getDataTypeName());
    }

    public static void newColumn(String name, Class<? extends DataType<?>> dataTypeClass, boolean isUnique, boolean isNullPossible) throws IOException {
        if (!ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new IOException("Before creating new column, you must create primary column");
        }
        if (ColumnParamsHashTable.isColumnNameUsed(name)) {
            throw new KeyAlreadyExistsException("Column '" + name + "' already exists");
        }
        DataType<?> dataType = Parser.getNullInstance(dataTypeClass);
        ColumnParamsHashTable.registerNewColumn(name, dataType.getDataTypeName(), isUnique, isNullPossible);
    }

    public static void cleanColumn(String name) throws IOException {
        try {
            ColumnParamsHashTable columnParamsToClean = new ColumnParamsHashTable(name);
            if (columnParamsToClean.isPrimaryColumn()) {
                throw new IOException("You can't clean primary column");
            }
            Column<? extends DataType<?>> columnToClean = exportColumnParamsToColumn(columnParamsToClean);
            columnToClean.cleanColumn();
        } catch (NameNotFoundException ignored) {}
    }

    public static void deleteColumn(String name) throws IOException {
        try {
            ColumnParamsHashTable columnParamsToDel = new ColumnParamsHashTable(name);
            if (columnParamsToDel.isPrimaryColumn()) {
                throw new IOException("You can't delete primary column");
            }
            Column<? extends DataType<?>> columnToDel = exportColumnParamsToColumn(columnParamsToDel);
            columnToDel.deleteColumn();
        } catch (NameNotFoundException ignored) {}
        ColumnParamsHashTable.deleteColumn(name);
    }

    private static <DT extends DataType<?>> Column<DT> exportColumnParamsToColumn(ColumnParamsHashTable columnParams) throws IOException {
        Class<DT> columnDataType = (Class<DT>) Parser.findDataTypeClass(columnParams.getDataType());
        if (columnParams.isOnlyUniqueValuesInColumn()) {
            return new UniqueColumn<>(columnParams.columnName, columnDataType);
        } else {
            return new SimpleColumn<>(columnParams.columnName, columnParams.isNullValuesPossible(), columnDataType);
        }
    }

    public static List<Column<? extends DataType<?>>> getColumns() throws IOException {
        List<Column<? extends DataType<?>>> columns = new ArrayList<>();
        for (ColumnParamsHashTable columnParams : ColumnParamsHashTable.getAllColumns()) {
            columns.add(exportColumnParamsToColumn(columnParams));
        }
        return columns;
    }

    public static <DT extends DataType<?>> Column<DT> getColumn(String name) throws NameNotFoundException, IOException {
        ColumnParamsHashTable columnParams = new ColumnParamsHashTable(name);
        return exportColumnParamsToColumn(columnParams);
    }

    public static <DT extends DataType<?>> UniqueColumn<DT> getPrimaryColumn() throws IOException, KeyException, NameNotFoundException {
        String primaryColumnName = ColumnParamsHashTable.getPrimaryColumnName();
        ColumnParamsHashTable primaryColumnParams = new ColumnParamsHashTable(primaryColumnName);
        return (UniqueColumn<DT>) exportColumnParamsToColumn(primaryColumnParams);
    }

}
