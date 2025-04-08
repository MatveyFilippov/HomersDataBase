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

    public static void fromBackup(File backup, Path pathToDataBase) throws IOException {
        raiseErrorIfInvalidBackupFile(backup.getAbsolutePath());
        ArchiveUtil.unzipDirectory(backup.toPath(), pathToDataBase);
        DataBase.open(pathToDataBase);
    }

    public static void fromBackup(File backup) throws IOException {
        String nameOfDataBase = Extension.HDBB.removeFromFilePath(backup.getName());
        Path pathToDataBase = Paths.get(backup.getParent(), nameOfDataBase);
        fromBackup(backup, pathToDataBase);
    }

}
