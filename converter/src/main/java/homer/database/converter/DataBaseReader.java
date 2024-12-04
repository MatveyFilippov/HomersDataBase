package homer.database.converter;

import homer.database.backend.DataBase;
import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseReader {
    public static String getDataBaseName() {
        Path dataBaseFolder = Paths.get(FileProcessor.pathToDataBaseRootDir);
        return dataBaseFolder.getFileName().toString();
    }

    public static String getPathToDataBase() {
        return FileProcessor.pathToDataBaseRootDir;
    }

    public static String getPathToParentDirOfDataBase() {
        Path parentDir = Paths.get(DataBaseReader.getPathToDataBase()).toAbsolutePath().getParent();
        return parentDir == null ? "" : parentDir.toString();
    }

    public static List<String> getHeaders() throws IOException, NameNotFoundException {
        List<String> headers = new ArrayList<>();
        for (String columnName : DataBase.getColumnNames()) {
            headers.add(DataBase.getColumnHeader(columnName));
        }
        return headers;
    }

    public static List<String> getLines(String sep) throws NameNotFoundException, IOException, KeyException {
        List<String> lines = new ArrayList<>();
        for (RecordUniqueID lineID : DataBase.getAllRecordsIds()) {
            List<String> line = new ArrayList<>();
            for (String columnName : DataBase.getColumnNames()) {
                line.add(DataBase.readValue(columnName, lineID).toString());
            }
            lines.add(String.join(sep, line));
        }
        return lines;
    }
}
