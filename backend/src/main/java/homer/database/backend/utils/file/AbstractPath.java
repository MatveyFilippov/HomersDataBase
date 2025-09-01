package homer.database.backend.utils.file;

import homer.database.backend.exceptions.FileOperationException;
import homer.database.backend.exceptions.NotConnectedException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AbstractPath {

    private static Path root;

    public static void setRoot(Path dir) {
        AbstractPath.root = dir.normalize().toAbsolutePath();
        PathManager.makeDirs(dir);
    }

    public static void unsetRoot() {
        root = null;
    }

    public static boolean isRootSet() {
        return root != null;
    }

    public static void cleanRoot() {
        if (!isRootSet()) {
            throw new NotConnectedException();
        }
        PathManager.cleanDir(root);
    }

    private Path path;

    private AbstractPath(Path path) {
        this.path = path;
    }

    public AbstractPath(String... path) {
        this(Paths.get(path[0], java.util.Arrays.copyOfRange(path, 1, path.length)));
    }

    private Path toRealPath() {
        if (!isRootSet()) {
            throw new NotConnectedException();
        }
        return root.resolve(path);
    }

    public void appendExtensionIfNotExists(String extension) {
        extension = extension.startsWith(".") ? extension : "." + extension;
        String fileName = path.getFileName().toString();
        if (!fileName.toLowerCase().endsWith(extension.toLowerCase())) {
            path = path.resolveSibling(fileName + extension);
        }
    }

    public RandomAccessFile getFile() {
        Path realPath = toRealPath();
        PathManager.makeDirs(realPath.getParent());
        try {
            return new RandomAccessFile(realPath.toFile(), "rw");
        } catch (Exception ex) {
            throw new FileOperationException("Can't open file", ex);
        }
    }

    public void delete() {
        PathManager.delete(toRealPath());
    }

    public AbstractPath child(String nameFileOrFolder) {
        return new AbstractPath(path.resolve(nameFileOrFolder));
    }

    public AbstractPath parent() {
        return new AbstractPath(path.getParent());
    }

}
