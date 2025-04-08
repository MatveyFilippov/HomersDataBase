package homer.database.converter.csv;

import homer.database.converter.HomerDataBaseExtender;
import homer.database.converter.Extension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Exporter {

    public static final String CSV_LINE_SEPARATOR = ",";

    public static File toCSV(File exportFile) throws IOException {
        HomerDataBaseExtender.throwIfReadingUnavailable();
        List<String> lines = new ArrayList<>();
        lines.add(String.join(CSV_LINE_SEPARATOR, ReadWriteUtil.getHeadersFromHomerDataBase()));
        ReadWriteUtil.getLinesFromHomerDataBase().forEach(line -> lines.add(String.join(CSV_LINE_SEPARATOR, line)));
        File csvExportFile = new File(Extension.CSV.appendToFilePath(exportFile.getAbsolutePath()));
        ReadWriteUtil.writer(csvExportFile, lines.toArray(new String[0]));
        return csvExportFile;
    }

    public static File toCSV(Path exportFolder, String fileName) throws IOException {
        return toCSV(new File(exportFolder.toFile(), fileName));
    }

    public static File toCSV() throws IOException {
        return toCSV(HomerDataBaseExtender.getPath().getParent(), HomerDataBaseExtender.getName());
    }

}
