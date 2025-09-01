package homer.database.backend.utils.storage;

import homer.database.backend.exceptions.BytesDictionaryFileOperationException;
import homer.database.backend.utils.file.AbstractPath;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class BytesDictionaryFile {

    public static final String HASH_DICT_FILE_EXTENSION = ".HBD";  // HomerBytesDictionary
    private final AbstractPath file;

    private static long getPosition(byte[] key) {
        final long hash = Hashing.murmur3_32_fixed().hashBytes(key).asInt();
        return Math.abs((hash & (FileHashStorage.STORAGE_SIZE - 1)) * Bucket.BUCKET_SIZE);
    }

    private static Iterator<Long> getPositionIterator() {
        return new Iterator<>() {
            private long currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < FileHashStorage.STORAGE_SIZE;
            }

            @Override
            public Long next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return currentIndex++ * Bucket.BUCKET_SIZE;
            }
        };
    }

    public BytesDictionaryFile(AbstractPath file) {
        file.appendExtensionIfNotExists(HASH_DICT_FILE_EXTENSION);
        this.file = file;
    }

    public void clean() {
        try (FileHashStorage storage = new FileHashStorage(file.getFile())) {
            storage.cleanAllBuckets();
        } catch (Exception ex) {
            throw new BytesDictionaryFileOperationException(
                    "Faced with problem while trying to clean file", ex
            );
        }
    }

    public void put(byte[] key, byte[] value) {
        Bucket bucket = new Bucket(key, value);
        long position = getPosition(key);
        try (FileHashStorage storage = new FileHashStorage(file.getFile())) {
            storage.writeBucket(position, bucket);
        } catch (Exception ex) {
            throw new BytesDictionaryFileOperationException(
                    "Faced with problem while trying to put data in file", ex
            );
        }
    }

    public void remove(byte[] key) {
        Objects.requireNonNull(key, "Key cannot be null");
        long position = getPosition(key);
        try (FileHashStorage storage = new FileHashStorage(file.getFile())) {
            storage.cleanBucket(position);
        } catch (Exception ex) {
            throw new BytesDictionaryFileOperationException(
                    "Faced with problem while trying to remove data from file", ex
            );
        }
    }

    private Bucket getBucket(byte[] key) {
        Objects.requireNonNull(key, "Key cannot be null");
        long position = getPosition(key);
        try (FileHashStorage storage = new FileHashStorage(file.getFile())) {
            return storage.readBucket(position);
        } catch (Exception ex) {
            throw new BytesDictionaryFileOperationException(
                    "Faced with problem while trying to get data from file", ex
            );
        }
    }

    public Optional<byte[]> get(byte[] key) {
        Bucket bucket = getBucket(key);
        if (bucket.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(bucket.getValue());
    }

    public boolean isKeyExists(byte[] key) {
        Bucket bucket = getBucket(key);
        return Arrays.equals(key, bucket.getKey());
    }

    private List<Bucket> getAllNotEmptyBuckets() {
        List<Bucket> buckets = new ArrayList<>();
        Iterator<Long> positions = getPositionIterator();
        try (FileHashStorage storage = new FileHashStorage(file.getFile())) {
            while (positions.hasNext()) {
                long position = positions.next();
                Bucket bucket = storage.readBucket(position);
                if (!bucket.isEmpty()) {
                    buckets.add(bucket);
                }
            }
        } catch (Exception ex) {
            throw new BytesDictionaryFileOperationException(
                    "Faced with problem while trying to get all not empty data from file", ex
            );
        }
        return buckets;
    }

    public byte[][] getAllKeys() {
        return getAllNotEmptyBuckets().stream()
                                      .map(Bucket::getKey)
                                      .toArray(byte[][]::new);
    }

    public byte[][] findKeysByValue(byte[] value) {
        Objects.requireNonNull(value, "Value cannot be null");
        return getAllNotEmptyBuckets().stream()
                                      .filter(bucket -> Arrays.equals(value, bucket.getValue()))
                                      .map(Bucket::getKey)
                                      .toArray(byte[][]::new);
    }

}
