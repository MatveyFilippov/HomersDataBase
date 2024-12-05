package homer.database.backend.engine.columns.helpers;

import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.SimpleColumn;
import homer.database.backend.engine.columns.UniqueColumn;
import homer.database.backend.engine.datatypes.helpers.DataTypes;
import homer.database.backend.engine.datatypes.helpers.DataType;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class ColumnsProcessor {
    public static void createPrimaryColumn(String name, DataTypes columnClass) throws IOException {
        if (ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new KeyAlreadyExistsException("Primary column already exists");
        }
        ColumnParamsHashTable.registerNewPrimaryColumn(name, columnClass.toString());
    }

    public static void newColumn(String name, DataTypes columnClass, boolean isUnique, boolean isNullPossible) throws IOException {
        if (!ColumnParamsHashTable.isPrimaryColumnExists()) {
            throw new IOException("Before creating new column, you must create primary column");
        }
        if (ColumnParamsHashTable.isNameUsed(name)) {
            throw new KeyAlreadyExistsException("Column '" + name + "' already exists");
        }
        ColumnParamsHashTable.registerNewColumn(name, columnClass.toString(), isUnique, isNullPossible);
    }

    public static void cleanColumn(String name) throws IOException {
        try {
            ColumnParamsHashTable columnParamsToClean = new ColumnParamsHashTable(name);
            if (columnParamsToClean.isPrimaryColumn()) {
                throw new IOException("You can't clean primary column");
            }
            Column<? extends DataType> columnToClean = exportColumnParamsToColumn(columnParamsToClean);
            columnToClean.cleanColumn();
        } catch (NameNotFoundException ignored) {}
    }

    public static void deleteColumn(String name) throws IOException {
        try {
            ColumnParamsHashTable columnParamsToDel = new ColumnParamsHashTable(name);
            if (columnParamsToDel.isPrimaryColumn()) {
                throw new IOException("You can't delete primary column");
            }
            Column<? extends DataType> columnToDel = exportColumnParamsToColumn(columnParamsToDel);
            columnToDel.deleteColumn();
        } catch (NameNotFoundException ignored) {}
        ColumnParamsHashTable.deleteColumn(name);
    }

    private static <DT extends DataType> Column<DT> exportColumnParamsToColumn(ColumnParamsHashTable columnParams) throws IOException {
        DataTypes columnDataType = DataTypes.valueOf(columnParams.getDataType());
        if (columnParams.isOnlyUniqueValuesInColumn()) {
            return new UniqueColumn<>(columnParams.columnName, columnDataType);
        } else {
            return new SimpleColumn<>(
                    columnParams.columnName, columnParams.isNullValuesPossible(), columnDataType
            );
        }
    }

    public static List<Column<? extends DataType>> getColumns() throws IOException {
        List<Column<? extends DataType>> columns = new ArrayList<>();
        for (ColumnParamsHashTable columnParams : ColumnParamsHashTable.getAllColumns()) {
            columns.add(exportColumnParamsToColumn(columnParams));
        }
        return columns;
    }

    public static <DT extends DataType> Column<DT> getColumn(String name) throws NameNotFoundException, IOException {
        ColumnParamsHashTable columnParams = new ColumnParamsHashTable(name);
        return exportColumnParamsToColumn(columnParams);
    }

    public static <DT extends DataType> UniqueColumn<DT> getPrimaryColumn() throws IOException, KeyException, NameNotFoundException {
        String primaryColumnName = ColumnParamsHashTable.getPrimaryColumnName();
        ColumnParamsHashTable primaryColumnParams = new ColumnParamsHashTable(primaryColumnName);
        return (UniqueColumn<DT>) exportColumnParamsToColumn(primaryColumnParams);
    }
}