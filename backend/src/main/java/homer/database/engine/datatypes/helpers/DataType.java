package homer.database.engine.datatypes.helpers;

public abstract class DataType {
    protected Object value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public boolean isNull() {
        return value == null;
    }

    protected abstract void init(String value);

    public DataType(String value) {
        init(value);
    }
}
