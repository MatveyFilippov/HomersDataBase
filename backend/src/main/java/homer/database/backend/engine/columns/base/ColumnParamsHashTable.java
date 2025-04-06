package homer.database.backend.engine.columns.base;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

class ColumnParamsHashTable {

    private static final FileProcessor primaryColumn = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "PrimaryColumn");
    private static final FileProcessor names = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnNames");
    private static final FileProcessor dataTypes = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnDataTypes");
    private static final FileProcessor columnsWithUniqueValues = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnsWithUniqueValues");
    private static final FileProcessor columnsWithNullValues = new FileProcessor(FileProcessor.Constants.HDBT_FOLDER_NAME, "ColumnsWithNullValues");
    public final String columnName;

    public static boolean isColumnNameUsed(String columnName) throws IOException {
        try (HashDict namesDict = new HashDict(names)) {
            return namesDict.isKeyExists(columnName);
        }
    }

    public static List<ColumnParamsHashTable> getAllColumns() throws IOException {
        List<ColumnParamsHashTable> result = new ArrayList<>();
        try (HashDict namesDict = new HashDict(names)) {
            for (String key : namesDict.getAllKeys()) {
                try {
                    result.add(new ColumnParamsHashTable(key));
                } catch (NameNotFoundException ignored) {
                }
            }
        }
        return result;
    }

    public static boolean isPrimaryColumnExists() throws IOException {
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            return primaryColumnDict.getAllKeys().size() == 1;
        }
    }

    public static String getPrimaryColumnName() throws IOException, KeyException {
        List<String> primaryColumnNames;
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            primaryColumnNames = primaryColumnDict.getAllKeys();
            if (primaryColumnNames.size() != 1) {
                throw new KeyException("Primary column not set yet...");
            }
            return primaryColumnNames.get(0);
        }
    }

    public static void registerNewColumn(String columnName, String dataType, boolean isUnique, boolean isNullPossible) throws IOException {
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

    public static void registerNewPrimaryColumn(String columnName, String dataType) throws IOException {
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
        } catch (IOException ignored) {}
    }

    private static boolean isAllColumnParamsExists(String columnName) throws IOException {
        try (HashDict namesDict = new HashDict(names); HashDict dataTypesDict = new HashDict(dataTypes)) {
            return namesDict.isKeyExists(columnName) && dataTypesDict.isKeyExists(columnName);
        }
    }

    public ColumnParamsHashTable(String columnName) throws IOException, NameNotFoundException {
        if (!isAllColumnParamsExists(columnName)) {
            throw new NameNotFoundException("Column not exists in table...");
        }
        this.columnName = columnName;
    }

    public String getDataType() throws IOException {
        try (HashDict dataTypesDict = new HashDict(dataTypes)) {
            return dataTypesDict.get(columnName, null);
        }
    }

    public boolean isPrimaryColumn() throws IOException {
        try (HashDict primaryColumnDict = new HashDict(primaryColumn)) {
            return primaryColumnDict.isKeyExists(columnName);
        }
    }

    public boolean isOnlyUniqueValuesInColumn() throws IOException {
        try (HashDict columnsWithUniqueValuesDict = new HashDict(columnsWithUniqueValues)) {
            return columnsWithUniqueValuesDict.isKeyExists(columnName);
        }
    }

    public boolean isNullValuesPossible() throws IOException {
        try (HashDict columnsWithNullValuesDict = new HashDict(columnsWithNullValues)) {
            return columnsWithNullValuesDict.isKeyExists(columnName);
        }
    }

}
