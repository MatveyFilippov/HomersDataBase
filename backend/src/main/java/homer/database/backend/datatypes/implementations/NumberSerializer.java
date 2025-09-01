package homer.database.backend.datatypes.implementations;

import homer.database.backend.datatypes.DataSerializer;
import homer.database.backend.exceptions.SerializingException;

public class NumberSerializer extends DataSerializer<Double> {

    @Override
    protected byte[] doSerialize(Double data) {
        long bits = Double.doubleToLongBits(data);
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (bits >>> (56 - i * 8));
        }
        return bytes;
    }

    @Override
    protected Double doDeserialize(byte[] data) {
        if (data.length != 8) {
            throw new SerializingException("Invalid byte array length for Double");
        }
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits = (bits << 8) | (data[i] & 0xff);
        }
        return Double.longBitsToDouble(bits);
    }

}
