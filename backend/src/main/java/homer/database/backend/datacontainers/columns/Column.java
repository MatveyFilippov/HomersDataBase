package homer.database.backend.datacontainers.columns;

import homer.database.backend.datatypes.DataSerializer;
import homer.database.backend.datatypes.DataType;
import homer.database.backend.exceptions.DataInColumnExistenceException;
import homer.database.backend.utils.file.AbstractPath;
import homer.database.backend.utils.storage.BytesDictionaryFile;
import java.util.Arrays;
import java.util.Objects;

public class Column<PK, V> {

    protected final BytesDictionaryFile values;
    protected final DataSerializer<PK> pkSerializer;
    protected final DataSerializer<V> valueSerializer;

    public Column(AbstractPath columnDir, DataType pkDataType, DataType valueDataType) {
        this.values = new BytesDictionaryFile(columnDir.child("Values"));
        this.pkSerializer = (DataSerializer<PK>) pkDataType.getSerializer();
        this.valueSerializer = (DataSerializer<V>) valueDataType.getSerializer();
    }

    public void writeValue(PK pk, V value) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");
        Objects.requireNonNull(value, "Value can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);
        byte[] valueByte = valueSerializer.serialize(value);

        values.put(pkByte, valueByte);
    }

    public V readValue(PK pk) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);
        byte[] valueByte = values.get(pkByte).orElseThrow(
                () -> new DataInColumnExistenceException("Value doesn't exists")
        );

        return valueSerializer.deserialize(valueByte);
    }

    public PK[] readPrimaryKeys(V value) {
        Objects.requireNonNull(value, "Value can't be null");

        byte[] valueByte = valueSerializer.serialize(value);

        return (PK[]) Arrays.stream(values.findKeysByValue(valueByte))
                            .map(pkSerializer::deserialize)
                            .toArray();
    }

    public PK[] readPrimaryKeys() {
        return (PK[]) Arrays.stream(values.getAllKeys())
                            .map(pkSerializer::deserialize)
                            .toArray();
    }

    public boolean isKeyExists(PK pk) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);

        return values.isKeyExists(pkByte);
    }

    public void deleteValue(PK pk) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);

        values.remove(pkByte);
    }

    public void cleanColumn() {
        values.clean();
    }

}
