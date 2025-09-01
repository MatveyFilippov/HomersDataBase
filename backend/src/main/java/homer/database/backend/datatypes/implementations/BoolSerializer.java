package homer.database.backend.datatypes.implementations;

import homer.database.backend.datatypes.DataSerializer;
import homer.database.backend.exceptions.SerializingException;

public class BoolSerializer extends DataSerializer<Boolean> {

    @Override
    protected byte[] doSerialize(Boolean data) {
        return new byte[] {(byte) (data ? 1 : 0)};
    }

    @Override
    protected Boolean doDeserialize(byte[] data) {
        if (data.length != 1) {
            throw new SerializingException("Invalid byte array length for Boolean");
        }
        return data[0] == 1;
    }

}
