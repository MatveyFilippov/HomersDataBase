package homer.database.converter;

import homer.database.backend.DataBase;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.NumberType;
import homer.database.backend.engine.datatypes.helpers.DataTypes;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;

class UsageExample {
    public static void main(String[] args) throws IOException, NameNotFoundException, KeyException {
        DataBase.setPathToDataBase("DataBaseToExportExample");
        DataBase.cleanTable();

        DataBase.createTable("ID", DataTypes.NUMBER);
        DataBase.createColumn("Name", DataTypes.STRING, false, false);
        DataBase.createColumn("Male", DataTypes.BOOL, false, true);

        NumberType id1 = new NumberType(1);
        RecordUniqueID newLineID = DataBase.createNewLine(id1);
        DataBase.writeValue("Name", newLineID, DataTypes.STRING.parseValue("Homer"));
        DataBase.writeValue("Male", newLineID, DataTypes.BOOL.parseValue("true"));

        NumberType id2 = new NumberType(2);
        DataBase.writeValue("Name", DataBase.createNewLine(id2), DataTypes.STRING.parseValue("Anonymous"));

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
