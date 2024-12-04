package homer.database.converter.backup;

import homer.database.backend.engine.FileProcessor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Exporter {
    private static String getDataBaseName() {
        Path dataBaseFolder = Paths.get(FileProcessor.pathToDataBaseRootDir);
        return dataBaseFolder.getFileName().toString();
    }

    private static String getPathToDataBase() {
        return FileProcessor.pathToDataBaseRootDir;
    }

    public static String getPathToBackupFile(String dirPathToPutBackupFile) {
        return BackupExtension.HDBB.appendExtensionToBackupFilePathIfNotExists(
                Paths.get(dirPathToPutBackupFile, getDataBaseName()).toString()
        );
    }

    public static void toBackupFile(String dirPathToPutBackupFile) throws IOException {
        ArchiveUtil.zipDirectory(getPathToDataBase(), getPathToBackupFile(dirPathToPutBackupFile));
    }
}
