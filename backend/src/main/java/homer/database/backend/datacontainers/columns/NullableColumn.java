package homer.database.backend.datacontainers.columns;

import homer.database.backend.datatypes.DataType;
import homer.database.backend.utils.file.AbstractPath;
import java.util.Arrays;
import java.util.Objects;

public class NullableColumn<PK, V> extends Column<PK, V> {

    public NullableColumn(AbstractPath columnDir, DataType pkDataType, DataType valueDataType) {
        super(columnDir, pkDataType, valueDataType);
    }

    @Override
    public void writeValue(PK pk, V value) {
        Objects.requireNonNull(pk, "PrimaryKey can't be null");

        byte[] pkByte = pkSerializer.serialize(pk);
        byte[] valueByte = valueSerializer.serialize(value);

        values.put(pkByte, valueByte);
    }

    @Override
    public PK[] readPrimaryKeys(V value) {
        byte[] valueByte = valueSerializer.serialize(value);

        return (PK[]) Arrays.stream(values.findKeysByValue(valueByte))
                            .map(pkSerializer::deserialize)
                            .toArray();
    }

}
