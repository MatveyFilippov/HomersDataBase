package homer.database.gui.misc;

import homer.database.gui.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class AppLogger {

    public static final AppLogger GLOBAL_LOGGER;
    private final Logger logger;

    static {
        ConsoleHandler defaultConsoleHandler = new ConsoleHandler();
        defaultConsoleHandler.setLevel(Level.ALL);
        defaultConsoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    thrown.printStackTrace(pw);
                    message += "\n" + sw;
                }
                return String.format(
                        "%s %tT - %s%n",
                        record.getLevel() == Level.SEVERE ? "ERROR" : record.getLevel().toString(),
                        record.getMillis(),
                        message
                );
            }
        });

        FileHandler defaultFileHandler;
        try {
            defaultFileHandler = new FileHandler(AppProperties.PATH_TO_LOG_FILE, Long.MAX_VALUE, 1, true);
        } catch (IOException e) {
            throw new RuntimeException("Can't init app logger", e);
        }
        defaultFileHandler.setLevel(Level.ALL);
        defaultFileHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    thrown.printStackTrace(pw);
                    message += "\n" + sw;
                }
                return String.format(
                        "%1$s %2$tY-%2$tm-%2$td %2$tT %3$s [%4$s] - %5$s%n",
                        record.getLevel() == Level.SEVERE ? "ERROR" : record.getLevel().toString(),
                        record.getMillis(),
                        record.getLoggerName(),
                        Thread.currentThread().getName(),
                        message
                );
            }
        });

        java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
        Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
        root.addHandler(defaultConsoleHandler);
        root.addHandler(defaultFileHandler);
        root.setLevel(Level.INFO);

        GLOBAL_LOGGER = getFor(AppLogger.class);
    }

    private AppLogger(Logger logger) {
        this.logger = logger;
    }

    private static String getErrorStackTrace(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static AppLogger getFor(Class<?> clazz) {
        return new AppLogger(LoggerFactory.getLogger(clazz));
    }

    public void errorOnlyWrite(String message, Throwable throwable) {
        if (throwable == null) {
            logger.error(message);
        } else {
            logger.error(message, throwable);
        }
    }

    private void notifyUserAboutError(String errorName, String message, Throwable throwable) {
        if (throwable == null) {
            AlertWindow.showError(errorName, message, true);
        } else {
            AlertWindow.showError(errorName, throwable.getLocalizedMessage(), true);
        }
    }

    public void error(String message, Throwable throwable) {
        errorOnlyWrite(message, throwable);
        notifyUserAboutError("Произошла ошибка", message, throwable);
    }

    public void error(String message) {
        error(message, null);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void info(String message) {
        logger.info(message);
    }

}
