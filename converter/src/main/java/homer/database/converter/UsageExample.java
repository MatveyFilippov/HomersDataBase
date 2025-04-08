package homer.database.converter;

import homer.database.backend.HomerDataBase;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.datatypes.base.implementations.BoolType;
import homer.database.backend.engine.datatypes.base.implementations.NumberType;
import homer.database.backend.engine.datatypes.base.implementations.StringType;
import java.io.File;
import java.io.IOException;

class UsageExample {

    public static void main(String[] args) throws IOException {
        HomerDataBase.open("HomerDataBaseToExportExample");
        HomerDataBase.cleanTable();

        HomerDataBase.createTable("ID", NumberType.class);
        HomerDataBase.createColumn("Name", StringType.class, false, false);
        HomerDataBase.createColumn("Male", BoolType.class, false, true);

        NumberType id1 = new NumberType(1);
        RecordUniqueID newLineID = HomerDataBase.createNewLine(id1);
        HomerDataBase.writeValue("Name", newLineID, new StringType("Homer"));
        HomerDataBase.writeValue("Male", newLineID, new BoolType(true));

        NumberType id2 = new NumberType(2);
        HomerDataBase.writeValue("Name", HomerDataBase.createNewLine(id2), new StringType("Anonymous"));

        homer.database.converter.csv.Exporter.toCSV();
        File backup = homer.database.converter.backup.Exporter.toBackup();

        HomerDataBase.deleteTable();

        homer.database.converter.backup.Importer.fromBackup(backup);

        for (String columnName : HomerDataBase.getColumnNames()) {
            System.out.print(HomerDataBase.getColumnHeader(columnName) + "\t");
        }
        System.out.println();
        for (RecordUniqueID lineID : HomerDataBase.getAllRecordIDs()) {
            for (String columnName : HomerDataBase.getColumnNames()) {
                System.out.print(HomerDataBase.readValue(columnName, lineID) + "\t");
            }
            System.out.println();
        }
    }

}
