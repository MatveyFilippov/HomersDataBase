package homer.database.engine.datatypes;

import homer.database.engine.datatypes.helpers.DataType;

public class NumberType extends DataType {
    public NumberType(Double value) {
        super(value.toString());
    }

    public NumberType(String value) {
        this(Double.valueOf(value));
    }

    @Override
    protected void init(String value) {
        if (value == null) {
            return;
        }
        super.value = Double.parseDouble(value);
    }
}
