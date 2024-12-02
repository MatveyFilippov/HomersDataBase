package homer.database.engine.columns.helpers;

import homer.database.engine.datatypes.helpers.DataType;

public class RecordUniqueID {
    private final DataType currentKey;

    public RecordUniqueID(DataType key) {
        currentKey = key;
    }

    public <DT extends DataType> DT toDataType() {
        return (DT) currentKey;
    }

    @Override
    public String toString() {
        return currentKey.toString();
    }
}
