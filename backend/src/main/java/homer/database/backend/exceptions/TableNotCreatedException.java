package homer.database.backend.exceptions;

public class TableNotCreatedException extends TableCreatingException {

    public TableNotCreatedException(String tableName) {
        super("Table '" + tableName + "' does not exist, create it before using", tableName);
    }

}
