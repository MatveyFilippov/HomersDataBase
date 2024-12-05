package homer.database.backend.engine;

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

    public static String pathToDataBaseRootDir;

    private Path filePath;

    public FileProcessor(String fileName, String ... parentFoldersFromRootDit) {
        if (pathToDataBaseRootDir == null) {
            throw new RuntimeException("Path to DataBase root dir must be set");
        }
        Path tempFilePath = Paths.get(pathToDataBaseRootDir);
        for (String nextFolder : parentFoldersFromRootDit) {
            tempFilePath = Paths.get(String.valueOf(tempFilePath), nextFolder);
        }
        tempFilePath = Paths.get(String.valueOf(tempFilePath), fileName);
        filePath = tempFilePath;
    }

    public RandomAccessFile getRandomAccessFile() throws IOException {
        makeDirs(filePath.getParent());
        return new RandomAccessFile(filePath.toString(), "rw");
    }

    public String getPathToFile() {
        return filePath.toAbsolutePath().toString();
    }

    public void appendExtensionIfNotExists(String fileExtension) {
        if (!fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension;
        }
        if (!filePath.toString().endsWith(fileExtension)) {
            filePath = Paths.get(filePath + fileExtension);
        }
    }

    public void deleteFile() {
        deleteFileOrDir(new File(filePath.toUri()));
    }

    private static void makeDirs(Path dirs) throws IOException {
        try {
            Files.createDirectories(dirs);
        } catch (FileAlreadyExistsException ignored) {}
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

    public static void deleteDir(String dirPath) {
        if (dirPath != null) {
            cleanDir(dirPath);
            deleteFileOrDir(new File(dirPath));
        }
    }

    public static void cleanDir(String dirPath) {
        if (dirPath != null) {
            cleanDir(new File(dirPath));
        }
    }

    public static String join(String ... members) {
        String first = members[0];
        String[] more = new String[members.length-1];
        System.arraycopy(members, 1, more, 0, members.length - 1);
        return Paths.get(first, more).toString();
    }

    public static String getAbsolute(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }
}
