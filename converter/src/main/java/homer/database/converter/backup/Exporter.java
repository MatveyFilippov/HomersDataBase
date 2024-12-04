package homer.database.converter.backup;

import homer.database.converter.DataBaseReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Exporter {
    private static String getPathToBackupFile(String dirPathToPutBackupFile) {
        return BackupExtension.HDBB.appendExtensionToBackupFilePathIfNotExists(
                Paths.get(dirPathToPutBackupFile, DataBaseReader.getDataBaseName()).toString()
        );
    }

    public static void toBackupFile(String dirPathToPutBackupFile) throws IOException {
        ArchiveUtil.zipDirectory(DataBaseReader.getPathToDataBase(), getPathToBackupFile(dirPathToPutBackupFile));
    }

    public static void toBackupFile() throws IOException {
        toBackupFile(DataBaseReader.getPathToParentDirOfDataBase());
    }
}
