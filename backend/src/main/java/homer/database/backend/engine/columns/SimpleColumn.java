package homer.database.backend.engine.columns;

import homer.database.backend.engine.FileProcessor;
import homer.database.backend.engine.HashDict;
import homer.database.backend.engine.columns.base.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleColumn<DT extends DataType<?>> implements Column<DT> {

    protected final String columnName;
    protected final boolean isNullValuesPossible;
    protected final Class<DT> columnDataTypeClass;
    protected final FileProcessor valuesHashTableFile;
    protected final String pathToColumnDirFromDBRoot;
    protected final DataType<?> dataTypeNullInstance;

    public SimpleColumn(String name, boolean canBeNull, Class<DT> dataTypeClass) {
        this.pathToColumnDirFromDBRoot = FileProcessor.join(
                FileProcessor.Constants.HDBC_FOLDER_NAME, name.replace(" ", "_")
        );
        this.valuesHashTableFile = new FileProcessor("Values", pathToColumnDirFromDBRoot);
        this.columnName = name;
        this.isNullValuesPossible = canBeNull;
        this.columnDataTypeClass = dataTypeClass;
        this.dataTypeNullInstance = Parser.getNullInstance(dataTypeClass);
    }

    @Override
    public void writeValue(RecordUniqueID recordUniqueID, DT value) throws IOException {
        if (value == null) {
            if (isNullValuesPossible) {
                deleteValue(recordUniqueID);
                return;
            }
            throw new NullPointerException("Value can't be null");
        }
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.put(recordUniqueID.toDatabase(), value.toDatabase());
        }
    }

    @Override
    public DT readValue(RecordUniqueID recordUniqueID) throws IOException {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            String value = values.get(recordUniqueID.toDatabase(), null);
            return Parser.getInstance(columnDataTypeClass, value);
        }
    }

    @Override
    public List<RecordUniqueID> getRecordsUniqueID(DT value) throws IOException {
        List<RecordUniqueID> recordsUniqueID = new ArrayList<>();
        if (value == null) {
            return recordsUniqueID;
        }
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            List<String> keys = values.findKeysByValue(value.toDatabase());
            for (String key : keys) {
                recordsUniqueID.add(new RecordUniqueID(Parser.getInstance(columnDataTypeClass, key)));
            }
        }
        return recordsUniqueID;
    }

    @Override
    public void deleteValue(RecordUniqueID recordUniqueID) throws IOException {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.remove(recordUniqueID.toDatabase());
        }
    }

    @Override
    public void cleanColumn() throws IOException {
        try (HashDict values = new HashDict(valuesHashTableFile)) {
            values.cleanDict();
        }
    }

    @Override
    public void deleteColumn() {
        FileProcessor.deleteDir(FileProcessor.join(FileProcessor.pathToDataBaseRootDir, pathToColumnDirFromDBRoot));
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
    public String getDatabaseHeader() {
        return getColumnName() + " (" + dataTypeNullInstance.getDataTypeName() + ")";
    }

    @Override
    public String toString() {
        return getColumnName() + " (" + dataTypeNullInstance.getDataTypeName() + ")";
    }

}
