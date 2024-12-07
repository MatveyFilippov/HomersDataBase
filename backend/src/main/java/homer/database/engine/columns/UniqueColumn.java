package homer.database.engine.columns;

import homer.database.engine.FileProcessor;
import homer.database.engine.HashDict;
import homer.database.engine.columns.helpers.RecordUniqueID;
import homer.database.engine.datatypes.helpers.DataType;
import homer.database.engine.datatypes.helpers.DataTypes;
import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UniqueColumn<DT extends DataType> extends Column<DT> {
    protected final FileProcessor idsHashTableFile;

    public UniqueColumn(String columnName, DataTypes dataType) throws IOException {
        super(columnName, false, dataType);
        idsHashTableFile = new FileProcessor(
                "Ids",
                FileProcessor.Constants.HDBC_FOLDER_NAME,
                columnName.replace(" ", "_")
        );
    }

    public List<DT> getAllValues() throws IOException {
        List<DT> values = new ArrayList<>();
        try (HashDict valuesDict = new HashDict(valuesHashTableFile)) {
            for (String value : valuesDict.getAllValues()) {
                values.add(dataType.parseValue(value));
            }
        }
        return values;
    }

    @Override
    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws IOException {
        if (value.isNull()) {
            throw new NullPointerException("Value can't be null");
        }
        try (HashDict ids = new HashDict(idsHashTableFile)) {
            String id = ids.get(value.toString(), null);
            if (id != null) {
                throw new KeyAlreadyExistsException(
                        "Value '" + value + "' already exists in column by id: " + id
                );
            }
            ids.put(value.toString(), recordUniqueID.toString());
        }
        super.writeValue(recordUniqueID, value);
    }

    @Override
    public List<RecordUniqueID> getRecordsUniqueID(DT value) throws IOException {
        List<RecordUniqueID> recordsUniqueID = new ArrayList<>();
        if (value == null) {
            return recordsUniqueID;
        }
        try (HashDict ids = new HashDict(idsHashTableFile)){
            String recordUniqueID = ids.get(value.toString(), null);
            if (recordUniqueID != null) {
                recordsUniqueID.add(new RecordUniqueID(dataType.parseValue(recordUniqueID)));
            }
        }
        return recordsUniqueID;
    }

    @Override
    public void deleteColumn() {
        super.deleteColumn();
        idsHashTableFile.deleteFile();
    }
}
