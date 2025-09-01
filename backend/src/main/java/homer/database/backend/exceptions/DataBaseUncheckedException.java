package homer.database.backend.exceptions;

public abstract class DataBaseUncheckedException extends RuntimeException {

    protected DataBaseUncheckedException(String message) {
        super(message);
    }

    protected DataBaseUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }

}
