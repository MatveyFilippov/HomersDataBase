package homer.database.backend.engine.columns;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.columns.helpers.RecordUniqueID;
import homer.database.backend.engine.datatypes.helpers.DataType;
import homer.database.backend.engine.datatypes.helpers.DataTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Column<DT extends DataType> {
    public final String columnName;
    protected final boolean canBeNull;
    public final DataTypes dataType;
    protected final FileProcessor valuesHashTableFile;
    protected final String pathToColumnDirFromDBRoot;

    public Column(String columnName, boolean canBeNull, DataTypes dataType) {
        pathToColumnDirFromDBRoot = FileProcessor.join(
                FileProcessor.Constants.HDBC_FOLDER_NAME,
                columnName.replace(" ", "_")
        );
        valuesHashTableFile = new FileProcessor("Values", pathToColumnDirFromDBRoot);

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
        FileProcessor.deleteDir(FileProcessor.join(FileProcessor.pathToDataBaseRootDir, pathToColumnDirFromDBRoot));
    }

    @Override
    public String toString() {
        return columnName + " (" + dataType + ")";
    }
}