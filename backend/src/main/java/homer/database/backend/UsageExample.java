package homer.database.backend;

import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.datatypes.base.implementations.NumberType;
import homer.database.backend.engine.datatypes.base.implementations.StringType;
import homer.database.backend.engine.exceptions.HomerDataBaseCheckedException;

class UsageExample {

    public static void main(String[] args) throws HomerDataBaseCheckedException {
        HomerDataBase.open("DataBaseExample");
        HomerDataBase.cleanTable();

        HomerDataBase.createTable("ID", NumberType.class);
        HomerDataBase.createColumn("Name", StringType.class, false, true);

        NumberType id1 = new NumberType(1);
        RecordUniqueID newLineID = HomerDataBase.createNewLine(id1);
        HomerDataBase.writeValue("Name", newLineID, new StringType("Homer"));

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
