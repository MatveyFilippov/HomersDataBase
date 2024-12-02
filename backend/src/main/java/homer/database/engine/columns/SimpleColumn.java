package homer.database.engine.columns;

import homer.database.engine.datatypes.helpers.DataType;
import homer.database.engine.datatypes.helpers.DataTypes;
import java.io.IOException;

public class SimpleColumn<DT extends DataType> extends Column<DT> {
    public SimpleColumn(String columnName, boolean canBeNull, DataTypes dataType) throws IOException {
        super(columnName, canBeNull, dataType);
    }
}
