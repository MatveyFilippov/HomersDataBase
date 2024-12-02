package homer.database.engine.datatypes;

import homer.database.engine.datatypes.helpers.DataType;

public class BoolType extends DataType {
    public BoolType(Boolean value) {
        super(value.toString());
    }

    public BoolType(String value) {
        super(value);
    }

    @Override
    public String toString() {
        if (super.value == null) {
            return null;
        }
        return (boolean) super.value ? "1" : "0";
    }

    @Override
    protected void init(String value) {
        if (value == null) {
            return;
        }
        if (value.equalsIgnoreCase("true") || value.charAt(0) == '1') {
            super.value = true;
        } else if (value.equalsIgnoreCase("false") || value.charAt(0) == '0') {
            super.value = false;
        }
    }
}
