package homer.database.backend.engine.datatypes;

public abstract class DataType<DT> {

    private DT value = null;

    public DataType() {}

    public DataType(DT value) {
        this.value = value;
    }

    public abstract String getDataTypeName();

    protected abstract DT toJavaValue(String value);

    protected abstract String toDatabaseValue(DT value);

    void fromDatabase(String value) {
        this.value = value == null ? null : toJavaValue(value);
    }

    public boolean isNull() {
        return value == null;
    }

    public DT toJava() {
        return isNull() ? null : toJavaValue(value.toString());
    }

    public String toDatabase() {
        return isNull() ? null : toDatabaseValue(value);
    }

    @Override
    public String toString() {
        return getDataTypeName() + ": '" + toDatabase() + "'";
    }

}
