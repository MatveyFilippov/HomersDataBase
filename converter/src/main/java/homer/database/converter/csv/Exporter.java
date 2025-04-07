package homer.database.converter.csv;

import homer.database.converter.DataBaseReader;
import homer.database.converter.Extension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Exporter {

    public static final String CSV_LINE_SEPARATOR = ",";

    private static void csvWriter(File csvFile, String... lines) throws IOException {
        try (FileWriter writer = new FileWriter(csvFile, false)) {
            for (String line : lines) {
                writer.append(line).append("\n");
            }
        }
    }

    private static File getCsvExportFile(File file) {
        file = file.getAbsoluteFile();
        String filePath = file.getAbsolutePath();
        if (file.isFile()) {
            return filePath.endsWith(Extension.CSV.toString()) ? file : new File(Extension.CSV.appendToFilePath(filePath));
        }
        return new File(Extension.CSV.appendToFilePath(Paths.get(filePath, DataBaseReader.getDataBaseName()).toString()));
    }

    public static File toCSV(File exportFileOrFolder) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(CSV_LINE_SEPARATOR, DataBaseReader.getHeaders()));
        DataBaseReader.getLines().forEach(line -> lines.add(String.join(CSV_LINE_SEPARATOR, line)));
        File csvExportFile = getCsvExportFile(exportFileOrFolder);
        csvWriter(csvExportFile, lines.toArray(new String[0]));
        return csvExportFile;
    }

    public static File toCSV() throws IOException {
        return toCSV(DataBaseReader.getPathToDataBase().getParent().toFile());
    }

}
