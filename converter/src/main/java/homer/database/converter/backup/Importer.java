package homer.database.converter.backup;

import homer.database.backend.DataBase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Importer {
    private static void raiseErrorIfInvalidBackupFile(String pathToBackupFile) throws IOException {
        if (!BackupExtension.HDBB.isFilePathEndsWithExtension(pathToBackupFile)) {
            throw new IOException("Invalid backup file, it must ends with: " + BackupExtension.HDBB);
        }
        if (!new File(pathToBackupFile).exists()) {
            throw new IOException("Backup file not exists");
        }
    }

    private static String getDataBaseName(String pathToBackupFile) {
        String fileName = Paths.get(pathToBackupFile).getFileName().toString();
        return BackupExtension.HDBB.removeExtensionFromBackupFilePathIfExists(fileName);
    }

    private static void setPathToDataBase(String pathToBackupFile, String dirToPlaceDataBase) {
        DataBase.setPathToDataBase(getPathToDataBase(pathToBackupFile, dirToPlaceDataBase));
    }

    private static String getPathToDataBase(String pathToBackupFile, String dirToPlaceDataBase) {
        return Paths.get(dirToPlaceDataBase, getDataBaseName(pathToBackupFile)).toString();
    }

    public static void fromBackupFile(String pathToBackupFile, String dirToPlaceDataBase) throws IOException {
        raiseErrorIfInvalidBackupFile(pathToBackupFile);
        ArchiveUtil.unzipDirectory(pathToBackupFile, getPathToDataBase(pathToBackupFile, dirToPlaceDataBase));
        setPathToDataBase(pathToBackupFile, dirToPlaceDataBase);
    }
}
