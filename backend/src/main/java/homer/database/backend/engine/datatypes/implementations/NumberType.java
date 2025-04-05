package homer.database.backend.engine.datatypes.implementations;

import homer.database.backend.engine.datatypes.DataType;

public class NumberType extends DataType<Double> {

    public NumberType() { super(); }

    public NumberType(Double value) { super(value); }

    public NumberType(Integer value) { super(value == null ? null : Double.valueOf(value)); }

    @Override
    public String getDataTypeName() {
        return "NUMBER";
    }

    @Override
    protected Double toJavaValue(String value) {
        return Double.parseDouble(value);
    }

    @Override
    protected String toDataBaseValue(Double value) {
        return value % 1 == 0 ? String.valueOf(value.intValue()) : String.valueOf(value);
    }

}
