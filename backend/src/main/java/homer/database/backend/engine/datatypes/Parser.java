package homer.database.backend.engine.datatypes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    private static final Map<String, Class<? extends DataType<?>>> dataTypes = new HashMap<>();

    public static void registerDataTypeClass(Class<? extends DataType<?>> dataType) {
        DataType<?> object = getNullInstance(dataType);
        dataTypes.put(object.getDataTypeName(), dataType);
    }

    public static Class<? extends DataType<?>> findDataTypeClass(String dataTypeName) {
        return dataTypes.get(dataTypeName);
    }

    public static Constructor<?> getConstructor(Class<? extends DataType<?>> dataType) {
        try {
            return dataType.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Internal exception: can't found default constructor for DataType", ex);
        }
    }

    public static <DT extends DataType<?>> DT getNullInstance(Class<? extends DataType<?>> dataType) {
        try {
            return (DT) getConstructor(dataType).newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Internal exception: can't create instance of DataType", ex);
        }
    }

    public static <DT extends DataType<?>> DT getInstance(Class<? extends DataType<?>> dataType, String value) {
        DT result = getNullInstance(dataType);
        result.fromDataBase(value);
        return result;
    }

}
