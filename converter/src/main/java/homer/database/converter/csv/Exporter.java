package homer.database.converter.csv;

import homer.database.converter.DataBaseReader;

import javax.naming.NameNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

public class Exporter {
    public static final String CSV_LINE_SEPARATOR = ", ";

    private static String appendExtensionToCsvFilePathIfNotExists(String filePath) {
        if (!filePath.endsWith(".CSV")) {
            filePath += ".CSV";
        }
        return filePath;
    }

    private static String getPathToCsvExportFile(String dirPathToPutCsvExportFile) {
        return appendExtensionToCsvFilePathIfNotExists(
                Paths.get(dirPathToPutCsvExportFile, DataBaseReader.getDataBaseName()).toString()
        );
    }

    private static void csvWriter(String csvFilePath, String... lines) throws IOException {
        try (FileWriter writer = new FileWriter(csvFilePath, false)) {
            for (String line : lines) {
                writer.append(line).append("\n");
            }
        }
    }

    public static void toCSV(String dirPathToPutCsvExportFile) throws NameNotFoundException, IOException, KeyException {
        List<String> linesTemp = new ArrayList<>();
        linesTemp.add(
                String.join(CSV_LINE_SEPARATOR, DataBaseReader.getHeaders())
        );
        linesTemp.addAll(DataBaseReader.getLines(CSV_LINE_SEPARATOR));
        csvWriter(getPathToCsvExportFile(dirPathToPutCsvExportFile), linesTemp.toArray(new String[0]));
    }

    public static void toCSV() throws NameNotFoundException, IOException, KeyException {
        toCSV(DataBaseReader.getPathToParentDirOfDataBase());
    }
}
