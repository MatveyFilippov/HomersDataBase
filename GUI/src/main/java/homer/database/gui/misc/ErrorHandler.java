package homer.database.gui.misc;

import homer.tastyworld.frontend.starterpack.base.exceptions.ControlledException;
import homer.tastyworld.frontend.starterpack.base.exceptions.DisplayedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.SelfLoggedException;
import homer.tastyworld.frontend.starterpack.base.exceptions.UnexpectedException;

public class ErrorHandler {

    private static final AppLogger logger = AppLogger.getFor(ErrorHandler.class);

    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        logger.error("An unhandled error occurred", throwable);
    }

}
