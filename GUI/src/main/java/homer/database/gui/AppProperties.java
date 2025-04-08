package homer.database.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppProperties {

    public static final String PATH_TO_DATA_DIR = Paths.get(
            System.getProperty("user.home"), ".HomerDataBaseAppData"
    ).toString();
    public static final String PATH_TO_LOG_FILE = PATH_TO_DATA_DIR + File.separator + "HomerDataBaseApp.log";
    public static final String DATABASE_NAME = "AppMainDataBase";
    public static final Path PATH_TO_DATABASE = Paths.get(PATH_TO_DATA_DIR, DATABASE_NAME);

}
