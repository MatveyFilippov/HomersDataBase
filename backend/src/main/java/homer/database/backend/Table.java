package homer.database.backend;

import homer.database.backend.datacontainers.columns.Column;
import homer.database.backend.datacontainers.columns.NullableColumn;
import homer.database.backend.datacontainers.columns.UniqueColumn;
import homer.database.backend.datacontainers.properties.ColumnProperties;
import homer.database.backend.datacontainers.properties.TableProperties;
import homer.database.backend.datatypes.DataType;
import homer.database.backend.exceptions.TableException;
import homer.database.backend.exceptions.TablePrimaryKeyException;
import homer.database.backend.utils.file.AbstractPath;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Table {

    public static final class Node {

        public final String column;
        public final Object value;

        public Node(String column, Object value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" + "column='" + column + '\'' + ", value=" + value + '}';
        }

    }

    private final AbstractPath tableDir;
    private final String tableName;

    Table(String tableName) {
        this.tableDir = new AbstractPath(tableName);
        this.tableName = tableName;
    }

    private UniqueColumn<Object, Object> getPrimaryColumn() {
        String primaryColumName = TableProperties.getPrimaryColumn(tableName);
        DataType primaryColumnDataType = ColumnProperties.dataType(tableName, primaryColumName);
        AbstractPath currentColumnDir = tableDir.child(primaryColumName);
        return new UniqueColumn<>(currentColumnDir, primaryColumnDataType, primaryColumnDataType);
    }

    private Column<Object, Object> getColumn(final String columnName) {
        DataType primaryColumnDataType = ColumnProperties.dataType(tableName, TableProperties.getPrimaryColumn(tableName));
        DataType currentColumnDataType = ColumnProperties.dataType(tableName, columnName);
        AbstractPath currentColumnDir = tableDir.child(columnName);

        if (ColumnProperties.isUnique(tableName, columnName)) {
            return new UniqueColumn<>(currentColumnDir, primaryColumnDataType, currentColumnDataType);
        } else if (ColumnProperties.isNullable(tableName, columnName)) {
            return new NullableColumn<>(currentColumnDir, primaryColumnDataType, currentColumnDataType);
        } else {
            return new Column<>(currentColumnDir, primaryColumnDataType, currentColumnDataType);
        }
    }

    private void checkColumnExistenceElseThrow(final String columnName) {
        if (!ColumnProperties.isExists(tableName, columnName)) {
            throw new TableException(
                    "Column '" + columnName + "' doesn't exist in table '" + tableName + "'", tableName
            );
        }
    }

    private void checkPrimaryKeyElseThrow(final Object primaryKey, boolean checkExistence) {
        DataType primaryColumnDataType = ColumnProperties.dataType(tableName, TableProperties.getPrimaryColumn(tableName));
        if (!primaryColumnDataType.isInstance(primaryKey)) {
            throw new TablePrimaryKeyException(
                    String.format(
                            "DataType of PrimaryKey for table '%s' must be '%s' (not '%s')",
                            tableName, primaryColumnDataType.getSerializableClass(), primaryKey.getClass()
                    ), tableName, primaryKey
            );
        }
        if (checkExistence && !getPrimaryColumn().isValueExists(primaryKey)) {
            throw new TablePrimaryKeyException(
                    "PrimaryKey '" + primaryKey + "' doesn't exist in table '" + tableName + "'", tableName, primaryKey
            );
        }
    }

    private void checkNodesElseThrow(Node[] nodes, String[] allowedColumnNames, boolean checkAllPresentedColumnAreNotRepeated, boolean checkAllAllowedColumnsPresent) {
        Set<String> allowedColumns = Set.of(allowedColumnNames);
        Set<String> encounteredColumns = checkAllPresentedColumnAreNotRepeated || checkAllAllowedColumnsPresent
                                         ? new HashSet<>() : null;

        for (Node node : nodes) {
            if (!ColumnProperties.isExists(tableName, node.column)) {
                throw new TableException(
                        "Column '" + node.column + "' doesn't exist in table '" + tableName + "'", tableName
                );
            }
            if (!allowedColumns.contains(node.column)) {
                throw new TableException(
                        "Column '" + node.column + "' not allowed for this operation in table '" + tableName + "'",
                        tableName
                );
            }
            if (checkAllPresentedColumnAreNotRepeated && !encounteredColumns.add(node.column)) {
                throw new TableException(
                        "Column '" + node.column + "' can't be repeated for this operation in table '" + tableName + "'",
                        tableName
                );
            }
            DataType columnDataType = ColumnProperties.dataType(tableName, (node.column));
            if (!columnDataType.isInstance(node.value)) {
                throw new TableException(
                        String.format(
                                "DataType for Column '%s' in table '%s' must be '%s' (not '%s')",
                                node.column, tableName, columnDataType.getSerializableClass(), node.value.getClass()
                        ), tableName
                );
            }
        }

        if (checkAllAllowedColumnsPresent && allowedColumns.size() != encounteredColumns.size()) {
            throw new TableException(
                    "For this operation in table '" + tableName + "' must be presented all columns from: " + allowedColumns,
                    tableName
            );
        }
    }

    public String[] getAllColumnNames() {
        return ColumnProperties.getAll(tableName);
    }
    
    public String getPrimaryColumnName() {
        return TableProperties.getPrimaryColumn(tableName);
    }

    public String[] getAllColumnNamesWithoutPrimary() {
        String[] allColumns = ColumnProperties.getAll(tableName);
        String primaryColumn = TableProperties.getPrimaryColumn(tableName);
        String[] result = new String[allColumns.length - 1];

        int index = 0;
        for (String column : allColumns) {
            if (!column.equals(primaryColumn)) {
                result[index++] = column;
            }
        }

        return result;
    }

    public DataType getColumnValuesDataType(final String columnName) {
        checkColumnExistenceElseThrow(columnName);
        return ColumnProperties.dataType(tableName, columnName);
    }

    public boolean isColumnValuesUnique(final String columnName) {
        checkColumnExistenceElseThrow(columnName);
        return ColumnProperties.isUnique(tableName, columnName);
    }

    public boolean isColumnValuesNullable(final String columnName) {
        checkColumnExistenceElseThrow(columnName);
        return ColumnProperties.isUnique(tableName, columnName);
    }

    public Object[] getAllPrimaryKeys() {
        return getPrimaryColumn().readValues();
    }

    public boolean isPrimaryKeyExists(final Object primaryKey) {
        checkPrimaryKeyElseThrow(primaryKey, false);
        return getPrimaryColumn().isValueExists(primaryKey);
    }

    public void newLine(final Object primaryKey, Node... data) {
        checkPrimaryKeyElseThrow(primaryKey, false);
        checkNodesElseThrow(data, getAllColumnNamesWithoutPrimary(), true, true);

        UniqueColumn<Object, Object> primaryColumn = getPrimaryColumn();
        if (primaryColumn.isValueExists(primaryKey)) {
            throw new TablePrimaryKeyException(
                    "PrimaryKey '" + primaryKey + "' already exists in table '" + tableName + "'",
                    tableName, primaryKey
            );
        }

        primaryColumn.writeValue(primaryKey, primaryKey);
        Arrays.stream(data)
              .forEach(node -> getColumn(node.column).writeValue(primaryKey, node.value));
    }

    public void editLine(final Object primaryKey, Node... data) {
        checkPrimaryKeyElseThrow(primaryKey, true);
        checkNodesElseThrow(data, getAllColumnNamesWithoutPrimary(),true, false);

        Arrays.stream(data)
              .forEach(node -> getColumn(node.column).writeValue(primaryKey, node.value));
    }

    public Node[] getLine(final Object primaryKey) {
        checkPrimaryKeyElseThrow(primaryKey, true);

        return Arrays.stream(getAllColumnNamesWithoutPrimary())
                     .map(column -> new Node(column, getColumn(column).readValue(primaryKey)))
                     .toArray(Node[]::new);
    }

    public void deleteLine(final Object primaryKey) {
        checkPrimaryKeyElseThrow(primaryKey, true);

        Arrays.stream(ColumnProperties.getAll(tableName))
              .forEach(column -> getColumn(column).deleteValue(primaryKey));
    }

    public Object[] findPrimaryKeysWhere(Node data) {
        checkNodesElseThrow(new Node[] {data}, getAllColumnNamesWithoutPrimary(),false, false);

        return getColumn(data.column).readPrimaryKeys(data.value);
    }

    public Object[] findPrimaryKeysWhereAnd(Node... data) {
        checkNodesElseThrow(data, getAllColumnNamesWithoutPrimary(), false, false);

        return Arrays.stream(data)
                     .map(node -> getColumn(node.column).readPrimaryKeys(node.value))
                     .map(Set::of)
                     .reduce((set1, set2) -> {
                         set1.retainAll(set2);
                         return set1;
                     })
                     .map(Set::toArray)
                     .orElse(new Object[0]);
    }

    public Object[] findPrimaryKeysWhereOr(Node... data) {
        checkNodesElseThrow(data, getAllColumnNamesWithoutPrimary(), false, false);

        return Arrays.stream(data)
                     .map(node -> getColumn(node.column).readPrimaryKeys(node.value))
                     .flatMap(Arrays::stream)
                     .distinct()
                     .toArray();
    }

    public Object getValue(final Object primaryKey, final String columnName) {
        checkColumnExistenceElseThrow(columnName);
        checkPrimaryKeyElseThrow(primaryKey, true);

        return getColumn(columnName).readValue(primaryKey);
    }

    public void clean() {
        Arrays.stream(ColumnProperties.getAll(tableName))
              .forEach(column -> getColumn(column).cleanColumn());
    }

    void cleanByDeleteDir() {
        tableDir.delete();
    }

}
