package homer.database.engine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcessor {
    public static class Constants {
        public static final String HASH_DICT_FILE_EXTENSION = ".HHD";  // HomerHashTable
        public static final String HDBC_FOLDER_NAME = "HomerDataBaseColumns";
        public static final String HDBT_FOLDER_NAME = "HomerDataBaseTable";
    }

    public static String pathToDataBaseRootDir = "";

    private Path filePath;

    public FileProcessor(String fileName, String ... parentFoldersFromRootDit) {
        Path tempFilePath = Paths.get(String.valueOf(pathToDataBaseRootDir));
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
        return filePath.toString();
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
        File file = new File(filePath.toUri());
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    private static void makeDirs(Path dirs) throws IOException {
        try {
            Files.createDirectories(dirs);
        } catch (FileAlreadyExistsException e) {
            // pass
        }
    }

    public static void deleteRootDir() {
        File file = new File(pathToDataBaseRootDir);
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    public static String join(String ... members) {
        String first = members[0];
        String[] more = new String[members.length-1];
        System.arraycopy(members, 1, more, 0, members.length - 1);
        return Paths.get(first, more).toString();
    }
}