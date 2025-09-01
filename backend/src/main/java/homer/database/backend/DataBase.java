package homer.database.backend;

import homer.database.backend.datacontainers.properties.ColumnProperties;
import homer.database.backend.datacontainers.properties.TableProperties;
import homer.database.backend.datatypes.DataType;
import homer.database.backend.exceptions.TableCreatingException;
import homer.database.backend.exceptions.TableNotCreatedException;
import homer.database.backend.utils.file.AbstractPath;
import java.nio.file.Path;
import java.util.Objects;

public final class DataBase {

    public static final class TableCreator {

        private final String tableName;

        TableCreator(String tableName) {
            try {
                TableProperties.create(tableName);
            } catch (Exception ex) {
                throw new TableCreatingException(ex, tableName);
            }
            this.tableName = tableName;
        }

        private void deleteCreatedTableAndTrow(Exception ex) {
            TableProperties.delete(tableName);
            throw new TableCreatingException(ex, tableName);
        }

        public TableCreator withColumn(String columnName, DataType dataType, Boolean isNullable, Boolean isUnique) {
            try {
                ColumnProperties.create(tableName, columnName, dataType, isNullable, isUnique);
            } catch (Exception ex) {
                deleteCreatedTableAndTrow(ex);
            }
            return this;
        }

        public TableCreator withPrimaryColumn(String primaryColumnName, DataType primaryColumnDataType) {
            withColumn(primaryColumnName, primaryColumnDataType, false, true);
            try {
                TableProperties.setPrimaryColumn(tableName, primaryColumnName);
            } catch (Exception ex) {
                deleteCreatedTableAndTrow(ex);
            }
            return this;
        }

        public Table getTable() {
            try {
                TableProperties.setFinal(tableName);
            } catch (Exception ex) {
                deleteCreatedTableAndTrow(ex);
            }
            return new Table(tableName);
        }

    }

    public static void connect(Path pathToDataBaseDir) {
        Objects.requireNonNull(pathToDataBaseDir, "Path to DataBase directory can't be null");
        AbstractPath.setRoot(pathToDataBaseDir);
    }

    public static boolean isConnected() {
        return AbstractPath.isRootSet();
    }

    public static void disconnect() {
        AbstractPath.unsetRoot();
    }

    public static void cleanAll() {
        AbstractPath.cleanRoot();
    }

    public static TableCreator createTable(String tableName) {
        return new TableCreator(tableName);
    }

    public static Table getTable(String tableName) {
        if (!TableProperties.isExists(tableName) || !TableProperties.isFinal(tableName)) {
            throw new TableNotCreatedException(tableName);
        }
        return new Table(tableName);
    }

    public static void deleteTable(String tableName) {
        Table table = getTable(tableName);
        table.cleanByDeleteDir();
        TableProperties.delete(tableName);
    }

    public static String[] getAllTableNames() {
        return TableProperties.getAll();
    }

}
