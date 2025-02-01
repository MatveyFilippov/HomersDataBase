package homer.database.gui;

import java.io.File;
import java.nio.file.Paths;

public class AppProperties {
    public static final String PATH_TO_DATA_DIR = Paths.get(
            System.getProperty("user.home"), ".HomerDataBaseAppData"
    ).toString();
    public static final String PATH_TO_LOG_FILE = PATH_TO_DATA_DIR + File.separator + "HomerDataBaseApp.log";
    public static final String DB_NAME = "AppMainDataBase";
}
