package homer.database.converter.backup;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.*;

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

    public static void zipDirectory(String sourceDir, String zipFilePath) throws IOException {
        Path sourcePath = Paths.get(sourceDir);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFilePath)))) {
            try (Stream<Path> walker = Files.walk(sourcePath).filter(path -> !Files.isDirectory(path))) {
                walker.forEach(path -> zipTaskForEachFile(zos, sourcePath, path));
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

    public static void unzipDirectory(String zipFilePath, String destDir) throws IOException {
        File destDirectory = new File(destDir);
        mkdirs(destDirectory);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDirectory, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    mkParentDirs(newFile);
                    unzipTaskForEachFile(zis, newFile);
                }
                zis.closeEntry();
            }
        }
    }
}