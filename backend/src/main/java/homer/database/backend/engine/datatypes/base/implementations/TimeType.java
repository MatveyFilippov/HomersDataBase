package homer.database.backend.engine.datatypes.base.implementations;

import homer.database.backend.engine.datatypes.DataType;
import homer.database.backend.engine.exceptions.catchable.InvalidValueException;
import java.util.Map;
import java.util.regex.Pattern;

public class TimeType extends DataType<Map<String, Integer>> {

    public static class TimeKeys {

        public static final String HOURS = "h";
        public static final String MINUTES = "m";

    }

    public TimeType() { super(); }

    public TimeType(int hours, int minutes) { super(Map.of(TimeKeys.HOURS, hours, TimeKeys.MINUTES, minutes)); }

    @Override
    public String getDataTypeName() {
        return "TIME";
    }

    @Override
    protected Map<String, Integer> toJavaValue(String value) throws InvalidValueException {
        if (Pattern.matches("^\\d{1,2}:\\d{1,2}$", value)) {
            final String[] splitHourMinute = value.split(":");
            return Map.of(
                    TimeKeys.HOURS, Integer.valueOf(splitHourMinute[0]),
                    TimeKeys.MINUTES, Integer.valueOf(splitHourMinute[1])
            );
        }
        throw new InvalidValueException(TimeType.class, value, "Can't parse time value from '" + value + "'");
    }

    @Override
    protected String toDataBaseValue(Map<String, Integer> value) {
        String hourStr = value.get(TimeKeys.HOURS).toString();
        String minuteStr = value.get(TimeKeys.MINUTES).toString();
        if (hourStr.length() == 1) {
            hourStr = "0" + hourStr;
        }
        if (minuteStr.length() == 1) {
            minuteStr = "0" + minuteStr;
        }
        return hourStr + ":" + minuteStr;
    }

}
