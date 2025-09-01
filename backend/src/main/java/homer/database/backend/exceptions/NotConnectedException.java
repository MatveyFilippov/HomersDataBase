package homer.database.backend.exceptions;

public class NotConnectedException extends DataBaseUncheckedException {

    public NotConnectedException() {
        super("Unable to work with a disconnected DataBase");
    }

}
