package homer.database.backend.engine.exceptions.catchable;

import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.exceptions.HomerDataBaseCheckedException;

public class ReadWriteValueException extends HomerDataBaseCheckedException {

    public final RecordUniqueID recordUniqueID;
    public final String columnName;

    public ReadWriteValueException(RecordUniqueID recordUniqueID, String columnName, String message) {
        super(message);
        this.recordUniqueID = recordUniqueID;
        this.columnName = columnName;
    }

    public ReadWriteValueException(RecordUniqueID recordUniqueID, String columnName, String message, Throwable cause) {
        super(message, cause);
        this.recordUniqueID = recordUniqueID;
        this.columnName = columnName;
    }

}
