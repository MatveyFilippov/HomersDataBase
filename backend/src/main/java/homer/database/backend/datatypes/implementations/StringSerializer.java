package homer.database.backend.datatypes.implementations;

import homer.database.backend.datatypes.DataSerializer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringSerializer extends DataSerializer<String> {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    protected byte[] doSerialize(String data) {
        return data.getBytes(CHARSET);
    }

    @Override
    protected String doDeserialize(byte[] data) {
        return new String(data, CHARSET);
    }

}
