package homer.database.engine.columns;

import homer.database.engine.FileProcessor;
import homer.database.engine.HashDict;
import homer.database.engine.columns.helpers.RecordUniqueID;
import homer.database.engine.datatypes.helpers.DataType;
import homer.database.engine.datatypes.helpers.DataTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Column<DT extends DataType> {
    public final String columnName;
    protected final boolean canBeNull;
    public final DataTypes dataType;
    protected final FileProcessor valuesHashTableFile;

    public Column(String columnName, boolean canBeNull, DataTypes dataType) throws IOException {
        valuesHashTableFile = new FileProcessor(
                "Values",
                FileProcessor.Constants.HDBC_FOLDER_NAME,
                columnName.replace(" ", "_")
        );

        this.columnName = columnName;
        this.canBeNull = canBeNull;
        this.dataType = dataType;
    }

    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws IOException {
        if (!canBeNull && value.isNull()) {
            throw new NullPointerException("Value can't be null");
        }
        try (HashDict values = new HashDict(valuesHashTableFile)){
            values.put(recordUniqueID.toString(), value.toString());
        }
    }

    public DT readValue(RecordUniqueID recordUniqueID) throws IOException {
        try (HashDict values = new HashDict(valuesHashTableFile)){
            String value = values.get(recordUniqueID.toString(), null);
            return dataType.parseValue(value);
        }
    }

    public List<RecordUniqueID> getRecordsUniqueID(DT value) throws IOException {
        List<RecordUniqueID> recordsUniqueID = new ArrayList<>();
        if (value == null) {
            return recordsUniqueID;
        }
        try (HashDict values = new HashDict(valuesHashTableFile)){
            List<String> keys = values.findKeysByValue(value.toString());
            for (String key : keys) {
                recordsUniqueID.add(new RecordUniqueID(dataType.parseValue(key)));
            }
        }
        return recordsUniqueID;
    }

    public void deleteValue(RecordUniqueID recordUniqueID) throws IOException {
        try (HashDict values = new HashDict(valuesHashTableFile)){
            values.remove(recordUniqueID.toString());
        }
    }

    public void deleteColumn() {
        valuesHashTableFile.deleteFile();
    }

    @Override
    public String toString() {
        return columnName + " (" + dataType + ")";
    }
}
