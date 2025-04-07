package homer.database.backend.engine.exceptions.catchable;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.HomerDataBaseCheckedException;

public class InvalidValueException extends HomerDataBaseCheckedException {

    public final Class<? extends DataType<?>> requiredDataType;
    public final Object value;

    public InvalidValueException(Class<? extends DataType<?>> requiredDataType, Object value, String message) {
        super(message);
        this.requiredDataType = requiredDataType;
        this.value = value;
    }

    public InvalidValueException(Class<? extends DataType<?>> requiredDataType, Object value, String message, Throwable cause) {
        super(message, cause);
        this.requiredDataType = requiredDataType;
        this.value = value;
    }

}
