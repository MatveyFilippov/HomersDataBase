package homer.database.backend.engine.datatypes;

public abstract class DataType<DT> {

    private DT value = null;

    public DataType() {}

    public DataType(DT value) {
        this.value = value;
    }

    public abstract String getDataTypeName();

    protected abstract DT toJavaValue(String value);

    protected abstract String toDataBaseValue(DT value);

    void fromDataBase(String value) {
        this.value = value == null ? null : toJavaValue(value);
    }

    public boolean isNull() {
        return value == null;
    }

    public DT toJava() {
        return isNull() ? null : toJavaValue(value.toString());
    }

    public String toDatBase() {
        return isNull() ? null : toDataBaseValue(value);
    }

    @Override
    public String toString() {
        return getDataTypeName() + ": '" + toDatBase() + "'";
    }

}
