package homer.database.backend.engine.columns.base;

import homer.database.backend.engine.datatypes.DataType;

public class RecordUniqueID {

    private final DataType<?> currentKey;

    public RecordUniqueID(DataType<?> key) {
        currentKey = key;
    }

    public <DT extends DataType<?>> DT toDataType() {
        return (DT) currentKey;
    }

    public String toDatabase() {
        return currentKey.toDatabase();
    }

    @Override
    public String toString() {
        return currentKey.toString();
    }

}
