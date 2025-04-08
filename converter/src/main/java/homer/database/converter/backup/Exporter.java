package homer.database.converter.backup;

import homer.database.converter.Extension;
import homer.database.converter.HomerDataBaseExtender;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Exporter {

    public static File toBackup(File exportFile) throws IOException {
        HomerDataBaseExtender.throwIfReadingUnavailable();
        File backupExportFile = new File(Extension.HDBB.appendToFilePath((exportFile.getAbsolutePath())));
        ArchiveUtil.zipDirectory(HomerDataBaseExtender.getPath(), backupExportFile.toPath());
        return backupExportFile;
    }

    public static File toBackup(Path exportFolder, String fileName) throws IOException {
        return toBackup(new File(exportFolder.toFile(), fileName));
    }

    public static File toBackup() throws IOException {
        return toBackup(HomerDataBaseExtender.getPath().getParent(), HomerDataBaseExtender.getName());
    }

}
