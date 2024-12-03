package homer.database.backend.engine.datatypes;

import homer.database.backend.engine.datatypes.helpers.DataType;

public class NumberType extends DataType {
    public NumberType(Double value) {
        super(value.toString());
    }

    public NumberType(Integer value) {
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