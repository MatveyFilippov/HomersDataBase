package homer.database.backend.engine.datatypes.base.implementations;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;

public class NumberType extends DataType<Double> {

    public NumberType() { super(); }

    public NumberType(Double value) { super(value); }

    public NumberType(Integer value) { super(value == null ? null : Double.valueOf(value)); }

    @Override
    public String getDataTypeName() {
        return "NUMBER";
    }

    @Override
    protected Double toJavaValue(String value) throws InvalidValueException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new InvalidValueException(NumberType.class, value, "Can't parse number value from '" + value + "'");
        }
    }

    @Override
    protected String toDataBaseValue(Double value) {
        return value % 1 == 0 ? String.valueOf(value.intValue()) : String.valueOf(value);
    }

}
