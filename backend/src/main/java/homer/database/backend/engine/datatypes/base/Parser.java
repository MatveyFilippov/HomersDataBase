package homer.database.backend.engine.datatypes.base;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.datatypes.base.implementations.StringType;
import homer.database.backend.engine.exceptions.HomerDataBaseUncheckedException;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    private static final Map<String, Class<? extends DataType<?>>> dataTypes = new HashMap<>();

    public static void registerDataTypeClass(Class<? extends DataType<?>> dataTypeClass) {
        String dataTypeName = getNullInstance(dataTypeClass).getDataTypeName();
        if (dataTypes.containsKey(dataTypeName)) {
            throw new HomerDataBaseUncheckedException("DataType with name '" + dataTypeName + "' already exists");
        }
        dataTypes.put(dataTypeName, dataTypeClass);
    }

    public static Class<? extends DataType<?>> findDataTypeClass(String dataTypeName) {
        return dataTypes.getOrDefault(dataTypeName, StringType.class);
    }

    public static Constructor<?> getConstructor(Class<? extends DataType<?>> dataType) {
        try {
            return dataType.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            throw new HomerDataBaseUncheckedException("Internal exception: can't found default constructor for DataType", ex);
        }
    }

    public static <DT extends DataType<?>> DT getNullInstance(Class<DT> dataType) {
        try {
            return (DT) getConstructor(dataType).newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new HomerDataBaseUncheckedException("Internal exception: can't create instance of DataType", ex);
        }
    }

    public static <DT extends DataType<?>> DT getInstance(Class<DT> dataType, String value) throws InvalidValueException {
        DT result = getNullInstance(dataType);
        DataType.setValueFromDataBase(result, value);
        return result;
    }

}
