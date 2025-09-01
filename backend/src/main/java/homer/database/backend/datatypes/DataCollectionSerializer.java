package homer.database.backend.datatypes;

import homer.database.backend.exceptions.SerializingException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;

public abstract class DataCollectionSerializer<E, C extends Collection<E>> extends DataSerializer<C> {

    protected final DataSerializer<E> elementSerializer;

    public DataCollectionSerializer(DataSerializer<E> elementSerializer) {
        this.elementSerializer = elementSerializer;
    }

    protected abstract C getEmptyCollection(int length);

    @Override
    protected final byte[] doSerialize(C data) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            byte[] elementsQTY = ByteBuffer.allocate(4).putInt(data.size()).array();
            byteStream.write(elementsQTY);

            for (E element : data) {
                byte[] elementBytes = elementSerializer.serialize(element);
                byte[] elementLength = ByteBuffer.allocate(4).putInt(elementBytes.length).array();
                byteStream.write(elementLength);
                byteStream.write(elementBytes);
            }

            return byteStream.toByteArray();
        } catch (Exception ex) {
            throw new SerializingException("Failed to serialize collection", ex);
        }
    }

    @Override
    protected final C doDeserialize(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);

            int elementsQTY = buffer.getInt();
            C elements = getEmptyCollection(elementsQTY);

            for (int i = 0; i < elementsQTY; i++) {
                int elementLength = buffer.getInt();
                byte[] elementBytes = new byte[elementLength];
                buffer.get(elementBytes);
                E element = elementSerializer.deserialize(elementBytes);
                elements.add(element);
            }

            return elements;
        } catch (Exception ex) {
            throw new SerializingException("Failed to deserialize collection", ex);
        }
    }

}
