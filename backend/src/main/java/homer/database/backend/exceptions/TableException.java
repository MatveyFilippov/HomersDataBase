package homer.database.backend.exceptions;

public class TableException extends DataBaseUncheckedException {

    public final String tableName;

    public TableException(String message, final String tableName) {
        super(message);
        this.tableName = tableName;
    }

    public TableException(String message, Throwable cause, final String tableName) {
        super(message, cause);
        this.tableName = tableName;
    }

}
