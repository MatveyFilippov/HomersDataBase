package homer.database.converter.csv;

import homer.database.backend.HomerDataBase;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ReadWriteUtil {

    public static void writer(File file, String... lines) throws IOException {
        try (FileWriter writer = new FileWriter(file, false)) {
            for (String line : lines) {
                writer.append(line).append("\n");
            }
        }
    }

    public static List<String> getHeadersFromHomerDataBase() throws ColumnExistenceException {
        List<String> headers = new ArrayList<>();
        for (String columnName : HomerDataBase.getColumnNames()) {
            headers.add(HomerDataBase.getColumnHeader(columnName));
        }
        return headers;
    }

    public static List<List<String>> getLinesFromHomerDataBase() throws ColumnExistenceException, ReadWriteValueException {
        List<List<String>> lines = new ArrayList<>();
        String[] columnNames = HomerDataBase.getColumnNames();
        for (RecordUniqueID lineID : HomerDataBase.getAllRecordIDs()) {
            List<String> line = new ArrayList<>();
            for (String columnName : columnNames) {
                line.add(HomerDataBase.readValue(columnName, lineID).toDatBase());
            }
            lines.add(line);
        }
        return lines;
    }

}
