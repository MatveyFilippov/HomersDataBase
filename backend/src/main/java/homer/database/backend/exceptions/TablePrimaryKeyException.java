package homer.database.backend.exceptions;

public class TablePrimaryKeyException extends TableException {

    public final Object tablePrimaryKey;

    public TablePrimaryKeyException(String message, final String tableName, final Object tablePrimaryKey) {
        super(message, tableName);
        this.tablePrimaryKey = tablePrimaryKey;
    }

}
