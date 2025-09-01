package homer.database.backend.datacontainers.properties;

import homer.database.backend.datatypes.DataSerializer;
import homer.database.backend.datatypes.DataType;
import homer.database.backend.utils.file.AbstractPath;
import homer.database.backend.utils.storage.BytesDictionaryFile;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class Properties<V> {

    public static final Properties<Set<String>> TABLES = new Properties<>(
            "tables",
            DataType.STRING.getCollectionSerializer(HashSet::new)
    );
    public static final Properties<Set<String>> COLUMNS = new Properties<>(
            "%s.columns",
            DataType.STRING.getCollectionSerializer(HashSet::new)
    );
    public static final Properties<String> TABLE_PRIMARY_COLUMN = new Properties<>(
            "%s.primary_column",
            (DataSerializer<String>) DataType.STRING.getSerializer()
    );
    public static final Properties<String> COLUMN_DATA_TYPE = new Properties<>(
            "%s.%s.data_type",
            (DataSerializer<String>) DataType.STRING.getSerializer()
    );
    public static final Properties<Boolean> COLUMN_IS_NULLABLE = new Properties<>(
            "%s.%s.is_nullable",
            (DataSerializer<Boolean>) DataType.BOOL.getSerializer()
    );
    public static final Properties<Boolean> COLUMN_IS_UNIQUE = new Properties<>(
            "%s.%s.is_unique",
            (DataSerializer<Boolean>) DataType.BOOL.getSerializer()
    );
    public static final Properties<Boolean> TABLE_IS_FINAL = new Properties<>(
            "%s.is_final",
            (DataSerializer<Boolean>) DataType.BOOL.getSerializer()
    );

    private static final BytesDictionaryFile GLOBAL = new BytesDictionaryFile(new AbstractPath("Properties"));
    private static final DataSerializer<String> keySerializer = (DataSerializer<String>) DataType.STRING.getSerializer();
    private final DataSerializer<V> valueSerializer;
    private final String keyPattern;

    private Properties(String keyPattern, DataSerializer<V> valueSerializer) {
        this.keyPattern = keyPattern;
        this.valueSerializer = valueSerializer;
    }

    private byte[] getKey(String... args) {
        return keySerializer.serialize(String.format(keyPattern, (Object[]) args));
    }

    public void put(V value, String... args) {
        GLOBAL.put(getKey(args), valueSerializer.serialize(value));
    }

    public Optional<V> get(String... args) {
        return GLOBAL.get(getKey(args)).map(valueSerializer::deserialize);
    }

    public void remove(String... args) {
        GLOBAL.remove(getKey(args));
    }

}
