package homer.database.engine.datatypes;

import homer.database.engine.datatypes.helpers.DataType;

public class TimeType extends DataType {
    public static TimeType create(final int hour, final int minute) {
        String hourStr = String.valueOf(hour);
        String minuteStr = String.valueOf(minute);
        if (hourStr.length() == 1) {
            hourStr = "0" + hourStr;
        }
        if (minuteStr.length() == 1) {
            minuteStr = "0" + minuteStr;
        }
        return new TimeType(hourStr + ":" + minuteStr);
    }

    public TimeType(String value) {
        super(value);
    }

    @Override
    protected void init(String value) {
        if (value == null) {
            return;
        }
        String[] splitHourMinute = value.split(":", 2);
        if (splitHourMinute.length == 2 && splitHourMinute[0].length() == 2 && splitHourMinute[1].length() == 2) {
            super.value = value;
        }
    }
}
