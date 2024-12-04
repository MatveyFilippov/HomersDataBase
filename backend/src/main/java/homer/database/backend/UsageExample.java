package homer.database.backend;

import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.NumberType;
import homer.database.backend.engine.datatypes.helpers.DataTypes;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.security.KeyException;

class UsageExample {
    public static void main(String[] args) throws IOException, NameNotFoundException, KeyException {
        DataBase.setPathToDataBase("DataBaseExample");

        DataBase.createTable("ID", DataTypes.NUMBER);
        DataBase.createColumn("Name", DataTypes.STRING, false, true);

        NumberType id1 = new NumberType(1);
        RecordUniqueID newLineID = DataBase.createNewLine(id1);
        DataBase.writeValue("Name", newLineID, DataTypes.STRING.parseValue("Homer"));

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
