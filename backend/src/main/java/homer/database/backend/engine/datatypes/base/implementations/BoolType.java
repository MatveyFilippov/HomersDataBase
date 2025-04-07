package homer.database.backend.engine.datatypes.base.implementations;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;

public class BoolType extends DataType<Boolean> {

    public BoolType() { super(); }

    public BoolType(Boolean value) { super(value); }

    @Override
    public String getDataTypeName() {
        return "BOOLEAN";
    }

    @Override
    protected Boolean toJavaValue(String value) throws InvalidValueException {
        if (value.equalsIgnoreCase("true") || value.charAt(0) == '1') {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.charAt(0) == '0') {
            return false;
        }
        throw new InvalidValueException(BoolType.class, value, "Can't parse bool value from '" + value + "'");
    }

    @Override
    protected String toDataBaseValue(Boolean value) {
        return value ? "1" : "0";
    }

}
