package homer.database.backend.engine;

import com.google.common.hash.Hashing;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HashDict implements AutoCloseable {

    public final int BUCKET_SIZE = 256;  // Max size of key + value
    public final int DICT_SIZE = 1024;  // Number of cells in dict
    private final RandomAccessFile fileHashDict;

    public HashDict(FileProcessor hashDictFile) throws IOException {
        hashDictFile.appendExtensionIfNotExists(FileProcessor.Constants.HASH_DICT_FILE_EXTENSION);
        this.fileHashDict = hashDictFile.getRandomAccessFile();
        if (fileHashDict.length() == 0) {
            cleanDict();
        }
    }

    private long getPosition(String key) {
        final long hash = Hashing.murmur3_32_fixed().hashString(key, StandardCharsets.UTF_8).asInt();
        return Math.abs(hash % DICT_SIZE) * BUCKET_SIZE;
    }

    private void cleanCell(long position) throws IOException {
        fileHashDict.seek(position);
        for (int i = 0; i < BUCKET_SIZE; i++) {
            fileHashDict.writeByte(0);
        }
    }

    private void writeKeyValue(long position, byte[] key, byte[] value) throws IOException {
        fileHashDict.seek(position);
        fileHashDict.write(key);
        fileHashDict.writeByte(0);
        fileHashDict.write(value);
    }

    private String[] getKeyValue(long position) throws IOException {
        fileHashDict.seek(position);

        byte[] buffer = new byte[BUCKET_SIZE];
        fileHashDict.read(buffer);

        String data = new String(buffer, StandardCharsets.UTF_8).trim();
        String[] parts = data.split("\0", 2);

        if (parts.length == 2) {
            return parts;
        } else {
            return new String[2];
        }
    }

    public void put(String key, String value) throws IOException {
        if (key.length() > 128 || value.length() > 128) {
            throw new IllegalArgumentException("Key or value is too long (max 128 chars)");
        }
        long position = getPosition(key);

        cleanCell(position);
        writeKeyValue(position, key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
    }

    public String get(String key, String defaultValueIfNotExists) throws IOException {
        String[] parts = getKeyValue(getPosition(key));

        if (parts[0] != null && parts[0].equals(key)) {
            return parts[1];
        } else {
            return defaultValueIfNotExists;
        }
    }

    public boolean isKeyExists(String key) throws IOException {
        String[] parts = getKeyValue(getPosition(key));
        return parts[0] != null && parts[0].equals(key);
    }

    public List<String> findKeysByValue(String value) throws IOException {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < DICT_SIZE; i++) {
            long position = (long) i * BUCKET_SIZE;
            String[] parts = getKeyValue(position);
            if (parts[1] != null && parts[1].equals(value)) {
                keys.add(parts[0]);
            }
        }
        return keys;
    }

    public List<String> getAllKeys() throws IOException {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < DICT_SIZE; i++) {
            long position = (long) i * BUCKET_SIZE;
            String[] parts = getKeyValue(position);
            if (parts[0] != null) {
                keys.add(parts[0]);
            }
        }
        return keys;
    }

    public List<String> getAllValues() throws IOException {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < DICT_SIZE; i++) {
            long position = (long) i * BUCKET_SIZE;
            String[] parts = getKeyValue(position);
            if (parts[1] != null) {
                values.add(parts[1]);
            }
        }
        return values;
    }

    public void cleanDict() throws IOException {
        for (int i = 0; i < DICT_SIZE * BUCKET_SIZE; i++) {
            fileHashDict.writeByte(0);
        }
    }

    public void remove(String key) {
        try {
            cleanCell(getPosition(key));
        } catch (IOException ignored) {}
    }

    @Override
    public void close() throws IOException {
        fileHashDict.close();
    }

}
