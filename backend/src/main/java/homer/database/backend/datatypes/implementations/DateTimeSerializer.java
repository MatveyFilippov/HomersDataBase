package homer.database.backend.datatypes.implementations;

import homer.database.backend.datatypes.DataSerializer;
import homer.database.backend.exceptions.SerializingException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeSerializer extends DataSerializer<OffsetDateTime> {

    private static final DataSerializer<String> stringSerializer = new StringSerializer();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    protected byte[] doSerialize(OffsetDateTime data) {
        return stringSerializer.serialize(data.format(DATE_TIME_FORMATTER));
    }

    @Override
    protected OffsetDateTime doDeserialize(byte[] data) throws SerializingException {
        try {
            return OffsetDateTime.parse(stringSerializer.deserialize(data), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new SerializingException("Failed to deserialize OffsetDateTime", ex);
        }
    }

}
