package homer.database.backend.utils.file;

import homer.database.backend.exceptions.FileOperationException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class PathManager {

    public static void createFile(Path file) {
        if (Files.exists(file)) {
            return;
        }
        try {
            Files.createFile(file);
        } catch (FileAlreadyExistsException ignored) {} catch (Exception ex) {
            throw new FileOperationException("Can't create file", ex);
        }
    }

    public static void makeDirs(Path dirs) {
        try {
            Files.createDirectories(dirs);
        } catch (FileAlreadyExistsException ignored) {} catch (Exception ex) {
            throw new FileOperationException("Can't make directories", ex);
        }
    }

    public static void cleanDir(Path path) {
        if (!Files.isDirectory(path)) {
            return;
        }
        try (Stream<Path> files = Files.list(path)) {
            files.forEach(PathManager::delete);
        } catch (Exception ex) {
            throw new FileOperationException("Can't clean dir", ex);
        }
    }

    public static void delete(Path path) {
        if (Files.isDirectory(path)) {
            cleanDir(path);
        }
        try {
            Files.delete(path);
        } catch (Exception ex) {
            throw new FileOperationException("Can't delete file", ex);
        }
    }

}
