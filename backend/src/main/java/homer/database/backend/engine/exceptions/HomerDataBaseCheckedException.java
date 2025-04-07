package homer.database.backend.engine.exceptions;

import java.io.IOException;

public abstract class HomerDataBaseCheckedException extends IOException {

    protected HomerDataBaseCheckedException(String message) { super(message); }

    protected HomerDataBaseCheckedException(String message, Throwable cause) { super(message, cause); }

}
