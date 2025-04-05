package homer.database.converter;

import homer.database.backend.DataBase;
import homer.database.backend.engine.columns.base.RecordUniqueID;
import homer.database.backend.engine.datatypes.implementations.BoolType;
import homer.database.backend.engine.datatypes.implementations.NumberType;
import homer.database.backend.engine.datatypes.implementations.StringType;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;

class UsageExample {

    public static void main(String[] args) throws IOException, NameNotFoundException, KeyException {
        DataBase.openTable("DataBaseToExportExample");
        DataBase.cleanTable();

        DataBase.createTable("ID", NumberType.class);
        DataBase.createColumn("Name", StringType.class, false, false);
        DataBase.createColumn("Male", BoolType.class, false, true);

        NumberType id1 = new NumberType(1);
        RecordUniqueID newLineID = DataBase.createNewLine(id1);
        DataBase.writeValue("Name", newLineID, new StringType("Homer"));
        DataBase.writeValue("Male", newLineID, new BoolType(true));

        NumberType id2 = new NumberType(2);
        DataBase.writeValue("Name", DataBase.createNewLine(id2), new StringType("Anonymous"));

        homer.database.converter.csv.Exporter.toCSV();
        homer.database.converter.backup.Exporter.toBackupFile();

        DataBase.deleteTable();

        homer.database.converter.backup.Importer.fromBackupFile("DataBaseToExportExample.HDBB", "");

        for (String columnName : DataBase.getColumnNames()) {
            System.out.print(DataBase.getColumnHeader(columnName) + "\t");
        }
        System.out.println();
        for (RecordUniqueID lineID : DataBase.getAllRecordsIds()) {
            for (String columnName : DataBase.getColumnNames()) {
                System.out.print(DataBase.readValue(columnName, lineID) + "\t");
            }
            System.out.println();
        }
    }

}
