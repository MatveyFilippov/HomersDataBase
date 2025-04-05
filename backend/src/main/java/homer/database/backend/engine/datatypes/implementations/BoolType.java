package homer.database.backend.engine.datatypes.implementations;

import homer.database.backend.engine.datatypes.DataType;

public class BoolType extends DataType<Boolean> {

    public BoolType() { super(); }

    public BoolType(Boolean value) { super(value); }

    @Override
    public String getDataTypeName() {
        return "BOOLEAN";
    }

    @Override
    protected Boolean toJavaValue(String value) {
        if (value.equalsIgnoreCase("true") || value.charAt(0) == '1') {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.charAt(0) == '0') {
            return false;
        }
        return null;
    }

    @Override
    protected String toDataBaseValue(Boolean value) {
        return value ? "1" : "0";
    }

}
