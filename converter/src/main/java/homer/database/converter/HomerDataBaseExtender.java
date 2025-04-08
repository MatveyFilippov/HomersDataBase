package homer.database.converter;

import homer.database.backend.HomerDataBase;
import java.nio.file.Path;

public class HomerDataBaseExtender {

    public static void throwIfReadingUnavailable() {
        if (!HomerDataBase.isOpen()) {
            throw new RuntimeException("The HomerDataBase can not be accessed because nothing is open");
        }
        if (!HomerDataBase.isTableCreated()) {
            throw new RuntimeException("The HomerDataBase can not be accessed because nothing is created");
        }
    }

    public static String getName() {
        return HomerDataBase.getOpened().getFileName().toString();
    }

    public static Path getPath() {
        return HomerDataBase.getOpened();
    }

}
