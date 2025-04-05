package homer.database.backend.engine.datatypes.implementations;

import homer.database.backend.engine.datatypes.DataType;

public class StringType extends DataType<String> {

    public StringType() { super(); }

    public StringType(String value) { super(value); }

    @Override
    public String getDataTypeName() {
        return "STRING";
    }

    @Override
    protected String toJavaValue(String value) {
        return value;
    }

    @Override
    protected String toDatabaseValue(String value) {
        return value;
    }

}
