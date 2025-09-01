package homer.database.backend.utils.storage;

import java.io.IOException;
import java.io.RandomAccessFile;

class FileHashStorage implements AutoCloseable {

    public static final int STORAGE_SIZE = 1 << 20;  // 2^20 = 1 048 576
    private static final Bucket EMPTY_BUCKET = Bucket.empty();
    private static final byte[] CLEAN_BUFFER = EMPTY_BUCKET.getKeyValueWithSep();
    private final RandomAccessFile file;

    public FileHashStorage(RandomAccessFile file) throws IOException {
        this.file = file;
        if (this.file.length() == 0) {
            cleanAllBuckets();
        }
    }

    public void cleanAllBuckets() throws IOException {
        file.seek(0);
        for (int i = 0; i < STORAGE_SIZE; i++) {
            file.write(CLEAN_BUFFER);
        }
    }

    public void writeBucket(long position, Bucket bucket) throws IOException {
        file.seek(position);
        file.write(bucket.getKeyValueWithSep());
    }

    public void cleanBucket(long position) throws IOException {
        file.seek(position);
        file.write(CLEAN_BUFFER);
    }

    public Bucket readBucket(long position) throws IOException {
        byte[] buffer = EMPTY_BUCKET.getKeyValueWithSep();
        file.seek(position);
        file.read(buffer);
        return Bucket.of(buffer);
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

}
