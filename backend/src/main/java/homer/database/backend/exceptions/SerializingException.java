package homer.database.backend.exceptions;

public class SerializingException extends DataBaseUncheckedException {

    public SerializingException(String message) {
        super(message);
    }

    public SerializingException(String message, Throwable cause) {
        super(message, cause);
    }

}
