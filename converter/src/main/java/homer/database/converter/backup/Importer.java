package homer.database.converter.backup;

import homer.database.backend.DataBase;
import homer.database.converter.Extension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Importer {

    private static void raiseErrorIfInvalidBackupFile(String pathToBackupFile) throws IOException {
        if (!Extension.HDBB.isFilePathEndsWithExtension(pathToBackupFile)) {
            throw new IOException("Invalid backup file, it must ends with: " + Extension.HDBB);
        }
        if (!new File(pathToBackupFile).exists()) {
            throw new IOException("Backup file not exists");
        }
    }

    private static String getDataBaseName(File backup) {
        return Extension.HDBB.removeFromFilePath(backup.getName());
    }

    private static Path getPathToDataBase(File backup, Path dirToPlaceDataBase) {
        return Paths.get(dirToPlaceDataBase.toAbsolutePath().toString(), getDataBaseName(backup));
    }

    public static void fromBackup(File backup, Path dirToPlaceDataBase) throws IOException {
        raiseErrorIfInvalidBackupFile(backup.getAbsolutePath());
        Path pathToDataBase = getPathToDataBase(backup, dirToPlaceDataBase);
        ArchiveUtil.unzipDirectory(backup.toPath(), pathToDataBase);
        DataBase.open(pathToDataBase);
    }

    public static void fromBackup(File backup) throws IOException {
        fromBackup(backup, backup.toPath().getParent());
    }

}
