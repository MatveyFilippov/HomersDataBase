package homer.database.backend.datatypes;

import homer.database.backend.exceptions.SerializingException;

public abstract class DataSerializer<T> {

    protected abstract byte[] doSerialize(T data) throws SerializingException;
    protected abstract T doDeserialize(byte[] data) throws SerializingException;

    public final byte[] serialize(T data) {
        if (data == null) {
            return new byte[0];
        }
        return doSerialize(data);
    }

    public final T deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return doDeserialize(data);
    }

}
