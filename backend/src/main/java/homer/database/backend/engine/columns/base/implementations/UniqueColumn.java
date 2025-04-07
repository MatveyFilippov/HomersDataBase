package homer.database.backend.engine.columns.base.implementations;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;

public class UniqueColumn<DT extends DataType<?>> extends SimpleColumn<DT> {

    protected final FileProcessor idsHashTableFile;

    public UniqueColumn(String columnName, Class<DT> dataTypeClass) {
        super(columnName, false, dataTypeClass);
        idsHashTableFile = new FileProcessor(columnDir.getFromRootDir(), "IDs");
    }

    @Override
    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws ReadWriteValueException {
        if (value == null) {
            throw new ReadWriteValueException(recordUniqueID, columnName, "Value can't be null");
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            String id = ids.get(value.toDatBase(), null);
            if (id != null) {
                throw new HomerDataBaseUncheckedException("Value '" + value + "' already exists in column by id: " + id);
            }
            deleteValue(recordUniqueID);
            ids.put(value.toDatBase(), recordUniqueID.toDatBase());
        }
        super.writeValue(recordUniqueID, value);
    }

    @Override
    public RecordUniqueID[] find(DT value) throws ReadWriteValueException {
        if (value == null) {
            return new RecordUniqueID[0];
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            String recordUniqueID = ids.get(value.toDatBase(), null);
            return recordUniqueID != null
                   ? new RecordUniqueID[] {RecordUniqueID.of(recordUniqueID)}
                   : new RecordUniqueID[0];
        } catch (InvalidValueException ex) {
            throw new ReadWriteValueException(null, columnName, "Can't read RecordUniqueID", ex);
        }
    }

    @Override
    public void deleteValue(RecordUniqueID recordUniqueID) {
        DT value = null;
        try {
            value = super.readValue(recordUniqueID);
        } catch (ReadWriteValueException ignored) {}
        if (value == null) {
            return;
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            ids.remove(value.toDatBase());
        }
        super.deleteValue(recordUniqueID);
    }

    @Override
    public void cleanColumn() {
        super.cleanColumn();
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            ids.cleanDict();
        }
    }

}
