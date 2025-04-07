package homer.database.backend.engine.columns;

import homer.database.backend.engine.columns.base.processor.ColumnsProcessor;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.base.Parser;
import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import homer.database.backend.engine.exceptions.catchable.ColumnExistenceException;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;

public class RecordUniqueID {

    private static Class<? extends DataType<?>> primaryColumnDataType;
    private final DataType<?> currentKey;

    private static void checkPrimaryColumnDataType() {
        if (primaryColumnDataType != null) {
            return;
        }
        try {
            RecordUniqueID.primaryColumnDataType = ColumnsProcessor.getPrimaryColumn().getColumnDataTypeClass();
        } catch (ColumnExistenceException ignored) {
            throw new HomerDataBaseUncheckedException("DataType class of PrimaryColumn doesn't set yet");
        }
    }

    public static RecordUniqueID of(String value) throws InvalidValueException {
        checkPrimaryColumnDataType();
        return new RecordUniqueID(Parser.getInstance(primaryColumnDataType, value));
    }

    public RecordUniqueID(DataType<?> key) {
        if (key == null || key.isNull()) {
            throw new HomerDataBaseUncheckedException("RecordUniqueID can't be null");
        }
        checkPrimaryColumnDataType();
        if (!key.getClass().isAssignableFrom(primaryColumnDataType)) {
            throw new HomerDataBaseUncheckedException(
                    "DataType class of RecordUniqueID must be same with DataType in PrimaryColumn"
            );
        }
        currentKey = key;
    }

    public <DT extends DataType<?>> DT toDataType() {
        return (DT) currentKey;
    }

    public String toDatBase() {
        return currentKey.toDatBase();
    }

    @Override
    public String toString() {
        return currentKey.toString();
    }

}
