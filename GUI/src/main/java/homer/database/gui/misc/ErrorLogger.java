package homer.database.gui.misc;

import homer.database.gui.AppProperties;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorLogger {
    public static void handleError(Exception toHandle) {
        appErrorHandler(Thread.currentThread(), toHandle);
    }

    public static void appErrorHandler(Thread thread, Throwable throwable) {
        String errorStackTrace = getErrorStackTrace(throwable);
        String errorThreadName = thread.getName();
        String errorCause = getCause(throwable);

        writeErrorToLog(errorThreadName, errorStackTrace);
        printErrorInTerminal(errorThreadName, errorStackTrace);
        showErrorInApp(errorCause);
    }

    private static String getErrorStackTrace(final Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private static String getCause(final Throwable throwable) {
        Throwable temp = throwable;
        Throwable result;
        do {
            result = temp;
            temp = result.getCause();
        } while (temp != null);
        return result.toString();
    }

    private static void writeErrorToLog(String errorThreadName, String errorStackTrace) {
        try {
            FileWriter fileWriter = new FileWriter(AppProperties.PATH_TO_LOG_FILE, true);
            PrintWriter errorLog = new PrintWriter(fileWriter);
            errorLog.println("Error occurred on thread: " + errorThreadName);
            errorLog.println("Stack Trace:");
            errorLog.println(errorStackTrace);
            errorLog.println("------------------------");
            errorLog.println();
            errorLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showErrorInApp(String errorCause) {
        ErrorAlert.showAlert("Произошла неожиданная ошибка.", errorCause);
    }

    private static void printErrorInTerminal(String errorThreadName, String errorStackTrace) {
        System.err.println("Exception occurred in thread: " + errorThreadName);
        System.err.println(errorStackTrace);
    }
}
