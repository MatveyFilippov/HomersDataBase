package homer.database.backend.engine.exceptions;

public class HomerDataBaseUncheckedException extends RuntimeException {

    public HomerDataBaseUncheckedException(String message) { super(message); }

    public HomerDataBaseUncheckedException(String message, Throwable cause) { super(message, cause); }

}
