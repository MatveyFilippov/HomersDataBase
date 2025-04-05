package homer.database.backend.engine.columns;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.columns.base.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.Parser;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueColumn<DT extends DataType<?>> extends SimpleColumn<DT> {

    protected final FileProcessor idsHashTableFile;

    public UniqueColumn(String columnName, Class<DT> dataTypeClass) {
        super(columnName, false, dataTypeClass);
        idsHashTableFile = new FileProcessor("Ids", pathToColumnDirFromDBRoot);
    }

    public Set<DT> getAllValues() throws IOException {
        Set<DT> values = new HashSet<>();
        try (HashDict valuesDict = new HashDict(valuesHashTableFile)) {
            for (String value : valuesDict.getAllValues()) {
                values.add(Parser.getInstance(columnDataTypeClass, value));
            }
        }
        return values;
    }

    @Override
    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws IOException {
        if (value == null) {
            throw new NullPointerException("Value can't be null");
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            String id = ids.get(value.toString(), null);
            if (id != null) {
                throw new KeyAlreadyExistsException("Value '" + value + "' already exists in column by id: " + id);
            }
            deleteValue(recordUniqueID);
            ids.put(value.toString(), recordUniqueID.toDatabase());
        }
        super.writeValue(recordUniqueID, value);
    }

    @Override
    public List<RecordUniqueID> getRecordsUniqueID(DT value) throws IOException {
        List<RecordUniqueID> recordsUniqueID = new ArrayList<>();
        if (value == null) {
            return recordsUniqueID;
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            String recordUniqueID = ids.get(value.toString(), null);
            if (recordUniqueID != null) {
                recordsUniqueID.add(new RecordUniqueID(Parser.getInstance(columnDataTypeClass, recordUniqueID)));
            }
        }
        return recordsUniqueID;
    }

    @Override
    public void deleteValue(RecordUniqueID recordUniqueID) throws IOException {
        DT value = super.readValue(recordUniqueID);
        if (value == null) {
            return;
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            ids.remove(value.toString());
        }
        super.deleteValue(recordUniqueID);
    }

    @Override
    public void cleanColumn() throws IOException {
        super.cleanColumn();
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            ids.cleanDict();
        }
    }

}
