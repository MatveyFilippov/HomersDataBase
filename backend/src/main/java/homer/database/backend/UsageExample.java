package homer.database.backend;

import homer.database.backend.datatypes.DataType;
import java.nio.file.Paths;
import java.util.Arrays;

class UsageExample {

    public static void main(String[] args) {
        DataBase.connect(Paths.get("HomerDataBaseExample/"));
        DataBase.cleanAll();

        Table tableCreated = DataBase.createTable("Example")
                                     .withPrimaryColumn("ID", DataType.NUMBER)
                                     .withColumn("Name", DataType.STRING, false, false)
                                     .getTable();

        double id1 = 1;
        tableCreated.newLine(id1, new Table.Node("Name", "Homer"));
        if (tableCreated.isPrimaryKeyExists(id1)) {
            System.out.println("Line with id '" + id1 + "' created!");
            Table.Node[] line = tableCreated.getLine(id1);
            System.out.println(Arrays.toString(line));
        }

        for (String tableName : DataBase.getAllTableNames()) {
            System.out.println("\nTable: " + tableName);
            Table tableExisted = DataBase.getTable(tableName);

            String[] columnNames = tableExisted.getAllColumnNames();

            System.out.print("|\t");
            for (String columnName : columnNames) {
                System.out.print(columnName + "\t|\t");
            }
            System.out.println();

            for (Object pk : tableExisted.getAllPrimaryKeys()) {
                System.out.print("|\t");
                for (String columnName : columnNames) {
                    System.out.print(tableExisted.getValue(pk, columnName) + "\t|\t");
                }
                System.out.println();
            }
        }
    }

}
