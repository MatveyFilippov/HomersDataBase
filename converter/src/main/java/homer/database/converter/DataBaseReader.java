package homer.database.converter;

import homer.database.backend.DataBase;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataBaseReader {

    public static void throwIfReadingUnavailable() {
        if (!DataBase.isOpen()) {
            throw new RuntimeException("Can't work with DataBase because nothing is open");
        }
    }

    public static String getDataBaseName() {
        return DataBase.getOpened().getFileName().toString();
    }

    public static Path getPathToDataBase() {
        return DataBase.getOpened();
    }

    public static List<String> getHeaders() throws ColumnExistenceException {
        List<String> headers = new ArrayList<>();
        for (String columnName : DataBase.getColumnNames()) {
            headers.add(DataBase.getColumnHeader(columnName));
        }
        return headers;
    }

    public static List<List<String>> getLines() throws ColumnExistenceException, ReadWriteValueException {
        List<List<String>> lines = new ArrayList<>();
        String[] columnNames = DataBase.getColumnNames();
        for (RecordUniqueID lineID : DataBase.getAllRecordIDs()) {
            List<String> line = new ArrayList<>();
            for (String columnName : columnNames) {
                line.add(DataBase.readValue(columnName, lineID).toDatBase());
            }
            lines.add(line);
        }
        return lines;
    }

}
