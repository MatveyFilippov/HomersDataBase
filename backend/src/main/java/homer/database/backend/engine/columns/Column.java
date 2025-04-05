package homer.database.backend.engine.columns;

import homer.database.backend.engine.columns.base.RecordUniqueID;
import homer.database.backend.engine.datatypes.DataType;
import java.io.IOException;
import java.util.List;

public interface Column<DT extends DataType<?>> {

    void writeValue(RecordUniqueID recordUniqueID, DT value) throws IOException;

    DT readValue(RecordUniqueID recordUniqueID) throws IOException;

    void deleteValue(RecordUniqueID recordUniqueID) throws IOException;

    List<RecordUniqueID> getRecordsUniqueID(DT value) throws IOException;

    void cleanColumn() throws IOException;

    void deleteColumn();

    String getColumnName();

    Class<DT> getColumnDataTypeClass();

    boolean isNullValuesPossible();

    String getDatabaseHeader();

}
