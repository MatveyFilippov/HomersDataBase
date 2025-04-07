package homer.database.backend.engine.datatypes;

import homer.database.backend.engine.exceptions.catchable.InvalidValueException;

public abstract class DataType<VC> {

    Object value = null;

    public DataType() {}

    public DataType(VC value) {
        this.value = value;
    }

    public abstract String getDataTypeName();

    protected abstract VC toJavaValue(String value) throws InvalidValueException;

    protected abstract String toDataBaseValue(VC value);

    public boolean isNull() {
        return value == null;
    }

    public VC toJava() {
        return (VC) value;
    }

    public String toDatBase() {
        return isNull() ? null : toDataBaseValue((VC) value);
    }

    @Override
    public String toString() {
        return getDataTypeName() + ": '" + toDatBase() + "'";
    }

    public static <DT extends DataType<?>> void setValueFromDataBase(DT nullDataType, String valueFromDataBase) throws InvalidValueException {
        nullDataType.value = valueFromDataBase == null ? null : nullDataType.toJavaValue(valueFromDataBase);
    }

}
