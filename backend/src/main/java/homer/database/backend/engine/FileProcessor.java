package homer.database.backend.engine;

import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcessor {

    public static class Constants {

        public static final String HASH_DICT_FILE_EXTENSION = ".HHD";  // HomerHashDictionary
        public static final String HDBC_FOLDER_NAME = "HomerDataBaseColumns";
        public static final String HDBT_FOLDER_NAME = "HomerDataBaseTable";

    }

    private static Path pathToDataBaseRootDir;
    private Path filePath;

    public static void set(Path pathToDataBaseRootDir) {
        FileProcessor.pathToDataBaseRootDir = pathToDataBaseRootDir.toAbsolutePath();
    }

    public static void unset() {
        FileProcessor.pathToDataBaseRootDir = null;
    }

    public static Path getPathToDataBaseRootDir() {
        return pathToDataBaseRootDir;
    }

    private static void makeDirs(Path dirs) {
        try {
            Files.createDirectories(dirs);
        } catch (FileAlreadyExistsException ignored) {} catch (IOException ex) {
            throw new HomerDataBaseUncheckedException("Can't create dir", ex);
        }
    }

    private static void deleteFileOrDir(File obj) {
        if (obj != null && obj.exists()) {
            obj.delete();
        }
    }

    private static void cleanDir(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDir(file);
                }
                deleteFileOrDir(file);
            }
        }
    }

    public static void cleanDir(Path dirPath) {
        if (dirPath != null) {
            cleanDir(dirPath.toAbsolutePath().toFile());
        }
    }

    public static void deleteDir(Path dirPath) {
        if (dirPath != null) {
            cleanDir(dirPath);
            deleteFileOrDir(dirPath.toAbsolutePath().toFile());
        }
    }

    public static Path toAbsolutePathFromRoot(String path) {
        return Paths.get(pathToDataBaseRootDir.toString(), path).toAbsolutePath();
    }

    public static Path toAbsolutePath(String path) {
        return Paths.get(path).toAbsolutePath();
    }

    public static Path toAbsolutePath(String... members) {
        String first = members[0];
        String[] more = new String[members.length - 1];
        System.arraycopy(members, 1, more, 0, members.length - 1);
        return Paths.get(first, more).toAbsolutePath();
    }

    public FileProcessor(String... pathsFromRootDit) {
        if (pathToDataBaseRootDir == null) {
            throw new HomerDataBaseUncheckedException("Path to DataBase root dir must be set");
        }
        filePath = Paths.get(pathToDataBaseRootDir.toString(), pathsFromRootDit).toAbsolutePath();
    }

    public RandomAccessFile getRandomAccessFile() {
        makeDirs(filePath.getParent());
        try {
            return new RandomAccessFile(filePath.toFile(), "rw");
        } catch (IOException ex) {
            throw new HomerDataBaseUncheckedException("Can't open file", ex);
        }
    }

    public Path getPathToFile() {
        return filePath;
    }

    public String getFromRootDir() {
        return filePath.toString().replace(pathToDataBaseRootDir.toString(), "");
    }

    public void appendExtensionIfNotExists(String fileExtension) {
        if (!fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension;
        }
        if (!filePath.toString().endsWith(fileExtension)) {
            filePath = Paths.get(filePath + fileExtension).toAbsolutePath();
        }
    }

    public void deleteFile() {
        deleteFileOrDir(new File(filePath.toUri()));
    }

}
