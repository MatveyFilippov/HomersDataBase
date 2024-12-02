package homer.database.engine.datatypes;

import homer.database.engine.datatypes.helpers.DataType;

public class StringType extends DataType {
    public StringType(String value) {
        super(value);
    }

    @Override
    protected void init(String value) {
        if (value == null) {
            return;
        }
        super.value = value;
    }
}
