package homer.database.backend.datacontainers.properties;

import homer.database.backend.exceptions.PropertiesException;
import homer.database.backend.exceptions.PropertiesExistenceException;
import java.util.HashSet;
import java.util.Set;

public class TableProperties {

    private static void setNames(Set<String> names) {
        Properties.TABLES.put(names);
    }

    private static Set<String> getNames() {
        return Properties.TABLES.get().orElse(new HashSet<>());
    }

    public static String[] getAll() {
        return getNames().toArray(new String[0]);
    }

    public static boolean isExists(String name) {
        return getNames().contains(name);
    }

    public static boolean isFinal(String name) {
        return Properties.TABLE_IS_FINAL.get(name).orElse(false);
    }

    public static boolean isPrimaryColumnExists(String name) {
        return Properties.TABLE_PRIMARY_COLUMN.get(name).isPresent();
    }

    public static void create(String name) {
        Set<String> names = getNames();
        if (!names.add(name)) {
            throw new PropertiesException("Table '" + name + "' already exists");
        }
        setNames(names);
    }

    public static void setFinal(String name) {
        if (!isPrimaryColumnExists(name)) {
            throw new PropertiesException("Table '" + name + "' can't be final without property 'PrimaryColumn'");
        }
        Properties.TABLE_IS_FINAL.put(true, name);
    }

    public static void removeFinal(String name) {
        Properties.TABLE_IS_FINAL.remove(name);
    }

    public static void setPrimaryColumn(String name, String columnName) {
        if (isFinal(name)) {
            throw new PropertiesException(
                    "Can't set 'PrimaryColumn' property on table '" + name + "' because it is final"
            );
        }
        if (isPrimaryColumnExists(name)) {
            throw new PropertiesException("Table '" + name + "' already has property 'PrimaryColumn'");
        }
        if (!ColumnProperties.isExists(name, columnName)) {
            throw new PropertiesException(
                    "Can't set 'PrimaryColumn' property on table '" + name + "' without an existing column '" + columnName + "'"
            );
        }
        Properties.TABLE_PRIMARY_COLUMN.put(columnName, name);
    }

    public static String getPrimaryColumn(String name) {
        return Properties.TABLE_PRIMARY_COLUMN.get(name).orElseThrow(() -> new PropertiesExistenceException(
                "Table '" + name + "' hasn't property 'PrimaryColumn'"
        ));
    }

    public static void removePrimaryColumn(String name) {
        if (isFinal(name)) {
            throw new PropertiesException(
                    "Can't remove 'PrimaryColumn' property on table '" + name + "' because it is final"
            );
        }
        Properties.TABLE_PRIMARY_COLUMN.remove(name);
    }

    public static void delete(String name) {
        Set<String> names = getNames();
        if (!names.remove(name)) {
            throw new PropertiesException("Table '" + name + "' does not exist");
        }
        removeFinal(name);
        removePrimaryColumn(name);
        ColumnProperties.deleteAll(name);
        setNames(names);
    }

}
