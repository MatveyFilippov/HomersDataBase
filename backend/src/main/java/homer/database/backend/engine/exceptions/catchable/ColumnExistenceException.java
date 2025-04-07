package homer.database.backend.engine.exceptions.catchable;

import homer.database.backend.engine.exceptions.HomerDataBaseCheckedException;

public class ColumnExistenceException extends HomerDataBaseCheckedException {

    public final String columnName;

    public ColumnExistenceException(String name, boolean isExists) {
        super("Column '" + name + "' " + (isExists ? "already" : "probably doesn't") + " exist");
        columnName = name;
    }

}
