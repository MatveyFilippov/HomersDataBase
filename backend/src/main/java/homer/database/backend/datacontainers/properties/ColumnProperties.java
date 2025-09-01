package homer.database.backend.datacontainers.properties;

import homer.database.backend.datatypes.DataType;
import homer.database.backend.exceptions.PropertiesException;
import homer.database.backend.exceptions.PropertiesExistenceException;
import java.util.HashSet;
import java.util.Set;

public class ColumnProperties {

    private static void setNames(String tableName, Set<String> names) {
        Properties.COLUMNS.put(names, tableName);
    }

    private static Set<String> getNames(String tableName) {
        return Properties.COLUMNS.get(tableName).orElse(new HashSet<>());
    }

    private static void removeNames(String tableName) {
        Properties.COLUMNS.remove(tableName);
    }

    private static void setDataType(String tableName, String name, String value) {
        Properties.COLUMN_DATA_TYPE.put(value, tableName, name);
    }

    private static String getDataType(String tableName, String name) {
        return Properties.COLUMN_DATA_TYPE.get(tableName, name).orElseThrow(() -> new PropertiesExistenceException(
                "Column '" + name + "' in table '" + tableName + "' hasn't property 'DataType'"
        ));
    }

    private static void removeDataType(String tableName, String name) {
        Properties.COLUMN_DATA_TYPE.remove(tableName, name);
    }

    private static void setIsNullable(String tableName, String name, Boolean value) {
        Properties.COLUMN_IS_NULLABLE.put(value, tableName, name);
    }

    private static Boolean getIsNullable(String tableName, String name) {
        return Properties.COLUMN_IS_NULLABLE.get(tableName, name).orElseThrow(() -> new PropertiesExistenceException(
                "Column '" + name + "' in table '" + tableName + "' hasn't property 'IsNullable'"
        ));
    }

    private static void removeIsNullable(String tableName, String name) {
        Properties.COLUMN_IS_NULLABLE.remove(tableName, name);
    }

    private static void setIsUnique(String tableName, String name, Boolean value) {
        Properties.COLUMN_IS_UNIQUE.put(value, tableName, name);
    }

    private static Boolean getIsUnique(String tableName, String name) {
        return Properties.COLUMN_IS_UNIQUE.get(tableName, name).orElseThrow(() -> new PropertiesExistenceException(
                "Column '" + name + "' in table '" + tableName + "' hasn't property 'IsUnique'"
        ));
    }

    private static void removeIsUnique(String tableName, String name) {
        Properties.COLUMN_IS_UNIQUE.remove(tableName, name);
    }

    public static String[] getAll(String tableName) {
        return getNames(tableName).toArray(new String[0]);
    }

    public static boolean isExists(String tableName, String name) {
        return getNames(tableName).contains(name);
    }

    public static void create(String tableName, String name, DataType dataType, Boolean isNullable, Boolean isUnique) {
        if (TableProperties.isFinal(tableName)) {
            throw new PropertiesException(
                    "Can't create new column '" + name + "' on table '" + tableName + "' because it is final"
            );
        }
        Set<String> names = getNames(tableName);
        if (!names.add(name)) {
            throw new PropertiesException("Column '" + name + "' already exists in table '" + tableName + "'");
        }
        if (isNullable && isUnique) {
            throw new PropertiesException("Column can't be both unique and nullable at the same time");
        }
        setNames(tableName, names);
        setDataType(tableName, name, dataType.toString());
        setIsNullable(tableName, name, isNullable);
        setIsUnique(tableName, name, isUnique);
    }

    public static DataType dataType(String tableName, String name) {
        if (!isExists(tableName, name)) {
            throw new PropertiesException("Column '" + name + "' does not exist in table '" + tableName + "'");
        }
        return DataType.valueOf(getDataType(tableName, name));
    }

    public static Boolean isNullable(String tableName, String name) {
        if (!isExists(tableName, name)) {
            throw new PropertiesException("Column '" + name + "' does not exist in table '" + tableName + "'");
        }
        return getIsNullable(tableName, name);
    }

    public static Boolean isUnique(String tableName, String name) {
        if (!isExists(tableName, name)) {
            throw new PropertiesException("Column '" + name + "' does not exist in table '" + tableName + "'");
        }
        return getIsUnique(tableName, name);
    }

    public static void delete(String tableName, String name) {
        if (TableProperties.isFinal(tableName)) {
            throw new PropertiesException(
                    "Can't delete column '" + name + "' on table '" + tableName + "' because it is final"
            );
        }
        Set<String> names = getNames(tableName);
        if (!names.remove(name)) {
            throw new PropertiesException("Column '" + name + "' does not exist in table '" + tableName + "'");
        }
        removeIsUnique(tableName, name);
        removeIsNullable(tableName, name);
        removeDataType(tableName, name);
        setNames(tableName, names);
    }

    protected static void deleteAll(String tableName) {
        getNames(tableName).forEach(name -> {
            removeIsUnique(tableName, name);
            removeIsNullable(tableName, name);
            removeDataType(tableName, name);
        });
        removeNames(tableName);
    }

}
