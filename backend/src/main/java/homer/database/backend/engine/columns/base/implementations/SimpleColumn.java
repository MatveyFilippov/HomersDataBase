package homer.database.backend.engine.columns.base.implementations;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.columns.Column;
import homer.database.backend.engine.columns.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.base.Parser;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;
import java.util.List;

public class SimpleColumn<DT extends DataType<?>> implements Column<DT> {

    protected final String columnName;
    protected final boolean isNullValuesPossible;
    protected final Class<DT> columnDataTypeClass;
    protected final FileProcessor columnDir;
    protected final FileProcessor valuesHashTableFile;
    protected final DataType<?> dataTypeNullInstance;

    public SimpleColumn(String name, boolean canBeNull, Class<DT> dataTypeClass) {
        this.columnDir = new FileProcessor(
                FileProcessor.Constants.HDBC_FOLDER_NAME, name.replace(" ", "_")
        );
        this.valuesHashTableFile = new FileProcessor(columnDir.getFromRootDir(), "Values");
        this.columnName = name;
        this.isNullValuesPossible = canBeNull;
        this.columnDataTypeClass = dataTypeClass;
        this.dataTypeNullInstance = Parser.getNullInstance(dataTypeClass);
    }

    @Override
    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws ReadWriteValueException {
        if (value == null) {
            if (isNullValuesPossible) {
                deleteValue(recordUniqueID);
                return;
            }
            throw new ReadWriteValueException(recordUniqueID, columnName, "Value can't be null");
        }
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.put(recordUniqueID.toDatBase(), value.toDatBase());
        }
    }

    @Override
    public DT readValue(RecordUniqueID recordUniqueID) throws ReadWriteValueException {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            String value = values.get(recordUniqueID.toDatBase(), null);
            if (value == null && !isNullValuesPossible) {
                throw new ReadWriteValueException(recordUniqueID, columnName, "Can't find value");
            }
            return Parser.getInstance(columnDataTypeClass, value);
        } catch (InvalidValueException ex) {
            throw new ReadWriteValueException(recordUniqueID, columnName, "Can't read value", ex);
        }
    }

    @Override
    public RecordUniqueID[] getRecordUniqueIDs() throws ReadWriteValueException {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            List<String> keys = values.getAllKeys();
            RecordUniqueID[] result = new RecordUniqueID[keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                try {
                    result[i] = RecordUniqueID.of(keys.get(i));
                } catch (InvalidValueException ex) {
                    throw new ReadWriteValueException(null, columnName, "Can't read RecordUniqueID", ex);
                }
            }
            return result;
        }
    }

    @Override
    public RecordUniqueID[] find(DT value) throws ReadWriteValueException {
        if (value == null) {
            throw new ReadWriteValueException(null, columnName, "Sorry, I can't find all nul values");
        }
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            List<String> keys = values.findKeysByValue(value.toDatBase());
            RecordUniqueID[] result = new RecordUniqueID[keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                try {
                    result[i] = RecordUniqueID.of(keys.get(i));
                } catch (InvalidValueException ex) {
                    throw new ReadWriteValueException(null, columnName, "Can't read value RecordUniqueID", ex);
                }
            }
            return result;
        }
    }

    @Override
    public void deleteValue(RecordUniqueID recordUniqueID) {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.remove(recordUniqueID.toDatBase());
        }
    }

    @Override
    public void cleanColumn() {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.cleanDict();
        }
    }

    @Override
    public void deleteColumn() {
        columnDir.deleteFile();
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public Class<DT> getColumnDataTypeClass() {
        return columnDataTypeClass;
    }

    @Override
    public boolean isNullValuesPossible() {
        return isNullValuesPossible;
    }

    @Override
    public String getDataBaseHeader() {
        return getColumnName() + " (" + dataTypeNullInstance.getDataTypeName() + ")";
    }

    @Override
    public String toString() {
        return getColumnName() + " (" + dataTypeNullInstance.getDataTypeName() + ")";
    }

}
