package homer.database.converter.backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class ArchiveUtil {

    private static void zipTaskForEachFile(ZipOutputStream zos, Path sourcePath, Path currentPath) {
        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(currentPath).toString());
        try {
            zos.putNextEntry(zipEntry);
            Files.copy(currentPath, zos);
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void zipDirectory(Path sourceDir, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            try (Stream<Path> walker = Files.walk(sourceDir).filter(path -> !Files.isDirectory(path))) {
                walker.forEach(path -> zipTaskForEachFile(zos, sourceDir, path));
            }
        }
    }

    private static void mkdirs(File dirs) {
        if (!dirs.exists() && !dirs.mkdirs()) {
            throw new RuntimeException("Can't create dir: " + dirs);
        }
    }

    private static void mkParentDirs(File file) {
        mkdirs(new File(file.getParent()));
    }

    private static void unzipTaskForEachFile(ZipInputStream zis, File file) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zis.read(buffer)) >= 0) {
                bos.write(buffer, 0, length);
            }
        }
    }

    public static void unzipDirectory(Path zipFile, Path destDir) throws IOException {
        File destDirFile = destDir.toFile();
        mkdirs(destDirFile);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDirFile, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    mkParentDirs(newFile);
                    unzipTaskForEachFile(zis, newFile);
                }
                zis.closeEntry();
            }
        }
    }

}
