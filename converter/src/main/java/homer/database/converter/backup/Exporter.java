package homer.database.converter.backup;

import homer.database.converter.Extension;
import homer.database.converter.DataBaseReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Exporter {

    private static File getBackupExportFile(File file) {
        file = file.getAbsoluteFile();
        String filePath = file.getAbsolutePath();
        if (file.isFile()) {
            return filePath.endsWith(Extension.HDBB.toString()) ? file : new File(Extension.CSV.appendToFilePath(filePath));
        }
        return new File(Extension.HDBB.appendToFilePath(Paths.get(filePath, DataBaseReader.getDataBaseName()).toString()));
    }

    public static File toBackup(File exportFileOrFolder) throws IOException {
        DataBaseReader.throwIfReadingUnavailable();
        File backupExportFile = getBackupExportFile(exportFileOrFolder);
        ArchiveUtil.zipDirectory(DataBaseReader.getPathToDataBase(), backupExportFile.toPath());
        return backupExportFile;
    }

    public static File toBackup() throws IOException {
        return toBackup(DataBaseReader.getPathToDataBase().getParent().toFile());
    }

}
