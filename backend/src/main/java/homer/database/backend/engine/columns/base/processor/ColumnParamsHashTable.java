package homer.database.backend.engine.columns.base.processor;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import java.util.Objects;

class ColumnParamsHashTable {

    private static final FileProcessor primaryColumn = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "PrimaryColumn");
    private static final FileProcessor names = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnNames");
    private static final FileProcessor dataTypes = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnDataTypes");
    private static final FileProcessor columnsWithUniqueValues = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnsWithUniqueValues");
    private static final FileProcessor columnsWithNullValues = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnsWithNullValues");
    public final String columnName;

    public static boolean isColumnNameUsed(String columnName) {
        try (HashDict namesDict = new HashDict(names)) {
            return namesDict.isKeyExists(columnName);
        }
    }

    public static ColumnParamsHashTable[] getAllColumns() {
        try (HashDict namesDict = new HashDict(names)) {
            return namesDict.getAllKeys().stream().map(
                    key -> {
                        try {
                            return new ColumnParamsHashTable(key);
                        } catch (ColumnExistenceException ignored) {
                            return null;
                        }
                    }
            ).filter(Objects::nonNull).toArray(ColumnParamsHashTable[]::new);
        }
    }

    public static boolean isPrimaryColumnExists() {
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            return primaryColumnDict.getAllKeys().size() == 1;
        }
    }

    public static String getPrimaryColumnName() throws ColumnExistenceException {
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            String primaryColumnName = primaryColumnDict.getAllKeys().get(0);
            if (primaryColumnName == null) {
                throw new ColumnExistenceException("PRIMARY", false);
            }
            return primaryColumnName;
        }
    }

    public static void registerNewColumn(String columnName, String dataType, boolean isUnique, boolean isNullPossible) {
        try (HashDict namesDict = new HashDict(names); HashDict dataTypesDict = new HashDict(dataTypes)) {
            namesDict.put(columnName, columnName);
            dataTypesDict.put(columnName, dataType);
        }
        if (isUnique) {
            try (HashDict columnsWithUniqueValuesDict = new HashDict(columnsWithUniqueValues)) {
                columnsWithUniqueValuesDict.put(columnName, columnName);
            }
        } else if (isNullPossible) {
            try (HashDict columnsWithNullValuesDict = new HashDict(columnsWithNullValues)) {
                columnsWithNullValuesDict.put(columnName, columnName);
            }
        }
    }

    public static void registerNewPrimaryColumn(String columnName, String dataType) {
        registerNewColumn(columnName, dataType, true, false);
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            primaryColumnDict.cleanDict();
            primaryColumnDict.put(columnName, columnName);
        }
    }

    public static void deleteColumn(String columnName) {
        try (
                HashDict namesDict = new HashDict(names);
                HashDict dataTypesDict = new HashDict(dataTypes);
                HashDict columnsWithUniqueValuesDict = new HashDict(columnsWithUniqueValues);
                HashDict columnsWithNullValuesDict = new HashDict(columnsWithNullValues);
                HashDict primaryColumnDict = new HashDict(primaryColumn)
        ) {
            namesDict.remove(columnName);
            dataTypesDict.remove(columnName);
            columnsWithUniqueValuesDict.remove(columnName);
            columnsWithNullValuesDict.remove(columnName);
            primaryColumnDict.remove(columnName);
        }
    }

    private static boolean isAllColumnParamsExists(String columnName) {
        try (HashDict namesDict = new HashDict(names); HashDict dataTypesDict = new HashDict(dataTypes)) {
            return namesDict.isKeyExists(columnName) && dataTypesDict.isKeyExists(columnName);
        }
    }

    public ColumnParamsHashTable(String columnName) throws ColumnExistenceException {
        if (!isAllColumnParamsExists(columnName)) {
            throw new ColumnExistenceException(columnName, false);
        }
        this.columnName = columnName;
    }

    public String getDataType() {
        try (HashDict dataTypesDict = new HashDict(dataTypes)) {
            return dataTypesDict.get(columnName, null);
        }
    }

    public boolean isPrimaryColumn() {
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            return primaryColumnDict.isKeyExists(columnName);
        }
    }

    public boolean isOnlyUniqueValuesInColumn() {
        try (HashDict columnsWithUniqueValuesDict = new HashDict(columnsWithUniqueValues)) {
            return columnsWithUniqueValuesDict.isKeyExists(columnName);
        }
    }

    public boolean isNullValuesPossible() {
        try (HashDict columnsWithNullValuesDict = new HashDict(columnsWithNullValues)) {
            return columnsWithNullValuesDict.isKeyExists(columnName);
        }
    }

}
