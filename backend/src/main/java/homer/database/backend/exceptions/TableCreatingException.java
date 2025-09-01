package homer.database.backend.exceptions;

public class TableCreatingException extends TableException {

    protected TableCreatingException(String message, final String tableName) {
        super(message, tableName);
    }

    public TableCreatingException(Throwable cause, final String tableName) {
        super("Faced with problem while creating '" + tableName + "' table", cause, tableName);
    }

}
