package homer.database.backend.datatypes;

import homer.database.backend.datatypes.implementations.BoolSerializer;
import homer.database.backend.datatypes.implementations.DateTimeSerializer;
import homer.database.backend.datatypes.implementations.NumberSerializer;
import homer.database.backend.datatypes.implementations.StringSerializer;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.function.Function;

public enum DataType {

    BOOL (Boolean.class, new BoolSerializer()),
    NUMBER (Double.class, new NumberSerializer()),
    STRING (String.class, new StringSerializer()),
    TIME (OffsetDateTime.class, new DateTimeSerializer());

    private final Class<?> serializable;
    private final DataSerializer<?> serializer;

    <T> DataType(Class<T> serializable, DataSerializer<T> serializer) {
        this.serializable = serializable;
        this.serializer = serializer;
    }

    public Class<?> getSerializableClass() {
        return serializable;
    }

    public boolean isInstance(Object o) {
        return o == null || serializable.isInstance(o);
    }

    public DataSerializer<?> getSerializer() {
        return serializer;
    }

    public <E, C extends Collection<E>> DataSerializer<C> getCollectionSerializer(Function<Integer, C> collectionFactory) {
        return new DataCollectionSerializer<>((DataSerializer<E>) serializer) {
            @Override
            protected C getEmptyCollection(int length) {
                return collectionFactory.apply(length);
            }
        };
    }

}
