package homer.database.backend.engine.columns;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.catchable.ReadWriteValueException;

public interface Column<DT extends DataType<?>> {

    void writeValue(RecordUniqueID recordUniqueID, DT value) throws ReadWriteValueException;

    DT readValue(RecordUniqueID recordUniqueID) throws ReadWriteValueException;

    boolean isExists(RecordUniqueID recordUniqueID);

    RecordUniqueID[] find(DT value) throws ReadWriteValueException;

    RecordUniqueID[] getRecordUniqueIDs() throws ReadWriteValueException;

    void deleteValue(RecordUniqueID recordUniqueID);

    void cleanColumn();

    void deleteColumn();

    String getColumnName();

    Class<DT> getColumnDataTypeClass();

    boolean isNullValuesPossible();

    String getDataBaseHeader();

}
