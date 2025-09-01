package homer.database.backend.utils.storage;

import homer.database.backend.exceptions.BucketException;
import java.util.Arrays;
import java.util.Objects;

class Bucket {

    public static final int BUCKET_SIZE = 1 << 12;  // Max size of key + value + service_info (2^12 = 4 096)
    public static final int LENGTH_FIELD_SIZE = Integer.BYTES; // 4 byte for info about key/value size
    public static final int HEADER_SIZE = 2 * LENGTH_FIELD_SIZE; // 8 byte (key_len + val_len)
    public static final int MAX_KEY_VALUE_SIZE = BUCKET_SIZE - HEADER_SIZE;  // Max size of key + value
    private final byte[] key;
    private final byte[] value;

    public Bucket(byte[] key, byte[] value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        if (key.length + value.length > MAX_KEY_VALUE_SIZE) {
            throw new BucketException("Bucket overflow (key+value exceeds maximum allowed size)");
        }
        this.key = key.clone();
        this.value = value.clone();
    }

    public byte[] getKey() {
        return key.clone();
    }

    public byte[] getValue() {
        return value.clone();
    }

    public byte[] getKeyValueWithSep() {
        byte[] bytesKeyValueWithSep = new byte[BUCKET_SIZE];

        System.arraycopy(intToBytes(key.length), 0, bytesKeyValueWithSep, 0, LENGTH_FIELD_SIZE);
        System.arraycopy(intToBytes(value.length), 0, bytesKeyValueWithSep, LENGTH_FIELD_SIZE, LENGTH_FIELD_SIZE);
        System.arraycopy(key, 0, bytesKeyValueWithSep, HEADER_SIZE, key.length);
        System.arraycopy(value, 0, bytesKeyValueWithSep, HEADER_SIZE + key.length, value.length);

        return bytesKeyValueWithSep;
    }

    public boolean isEmpty() {
        return key.length == 0 && value.length == 0;
    }

    public static Bucket empty() {
        return new Bucket(new byte[0], new byte[0]);
    }

    public static Bucket of(byte[] bytesKeyValueWithSep) {
        Objects.requireNonNull(bytesKeyValueWithSep, "Input byte array cannot be null");
        if (bytesKeyValueWithSep.length != BUCKET_SIZE) {
            throw new BucketException("Invalid bucket size. Expected: " + BUCKET_SIZE);
        }

        int keyLen = bytesToInt(bytesKeyValueWithSep, 0);
        int valLen = bytesToInt(bytesKeyValueWithSep, LENGTH_FIELD_SIZE);
        if (keyLen < 0 || valLen < 0 || keyLen + valLen > MAX_KEY_VALUE_SIZE) {
            throw new BucketException("Corrupted bucket data");
        }

        byte[] key = Arrays.copyOfRange(bytesKeyValueWithSep, HEADER_SIZE, HEADER_SIZE + keyLen);
        byte[] value = Arrays.copyOfRange(bytesKeyValueWithSep, HEADER_SIZE + keyLen, HEADER_SIZE + keyLen + valLen);

        return new Bucket(key, value);
    }

    static byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    static int bytesToInt(byte[] bytes, int offset) {
        return (
                ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF)
        );
    }

}
