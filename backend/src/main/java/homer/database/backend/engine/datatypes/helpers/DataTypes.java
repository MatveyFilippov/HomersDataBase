package homer.database.backend.engine.datatypes.helpers;

import homer.database.backend.engine.datatypes.BoolType;
import homer.database.backend.engine.datatypes.NumberType;
import homer.database.backend.engine.datatypes.StringType;
import homer.database.backend.engine.datatypes.TimeType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DataTypes {

    BOOL (BoolType.class),
    NUMBER (NumberType.class),
    STRING (StringType.class),
    TIME (TimeType.class);

    public final Class<? extends DataType> dataTypeClass;

    DataTypes(Class<? extends DataType> dataTypeClass) {
        this.dataTypeClass = dataTypeClass;
    }

    public <DT> DT parseValue(String value) {
        try {
            Constructor<?> constructor = dataTypeClass.getConstructor(String.class);
            return (DT) constructor.newInstance(value);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}