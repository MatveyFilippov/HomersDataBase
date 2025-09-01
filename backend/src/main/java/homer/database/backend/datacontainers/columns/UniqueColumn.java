package homer.database.backend.datacontainers.columns;

import homer.database.backend.datatypes.DataType;
import homer.database.backend.exceptions.DataInColumnExistenceException;
import homer.database.backend.utils.file.AbstractPath;
import homer.database.backend.utils.storage.BytesDictionaryFile;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UniqueColumn<PK, V> extends Column<PK, V> {

    protected final BytesDictionaryFile keys;

    public UniqueColumn(AbstractPath columnDir, DataType pkDataType, DataType valueDataType) {
        super(columnDir, pkDataType, valueDataType);
        this.keys = new BytesDictionaryFile(columnDir.child("Keys"));
    }

    @Override
    public void writeValue(PK pk, V value) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");
        Objects.requireNonNull(value, "Value can't be null");

        byte[] valueByte = valueSerializer.serialize(value);
        if (keys.isKeyExists(valueByte)) {
            throw new DataInColumnExistenceException("Value already exists");
        }
        byte[] pkByte = pkSerializer.serialize(pk);

        keys.put(valueByte, pkByte);
        values.put(pkByte, valueByte);
    }

    public V[] readValues() {
        return (V[]) Arrays.stream(keys.getAllKeys())
                           .map(valueSerializer::deserialize)
                           .toArray();
    }

    private Optional<PK> readOptionalPrimaryKey(V value) {
        Objects.requireNonNull(value, "Value can't be null");

        byte[] valueByte = valueSerializer.serialize(value);

        return keys.get(valueByte).map(pkSerializer::deserialize);
    }

    public PK readPrimaryKey(V value) {
        return readOptionalPrimaryKey(value).orElseThrow(() -> new DataInColumnExistenceException("Key doesn't exists"));
    }

    @Deprecated
    @Override
    public PK[] readPrimaryKeys(V value) {
        return (PK[]) readOptionalPrimaryKey(value).map(List::of).orElse(List.of()).toArray();
    }

    public boolean isValueExists(V value) {
        Objects.requireNonNull(value, "Value can't be null");

        byte[] valueByte = valueSerializer.serialize(value);

        return keys.isKeyExists(valueByte);
    }

    @Override
    public void deleteValue(PK pk) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);

        values.get(pkByte).ifPresent(valueByte ->  {
            values.remove(pkByte);
            keys.remove(valueByte);
        });
    }

    @Override
    public void cleanColumn() {
        keys.clean();
        super.cleanColumn();
    }
}