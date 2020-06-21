package pl.edu.agh.geotime.domain.enumeration;

import java.time.ZonedDateTime;
import java.util.Optional;

public enum ClassFrequency {
    SINGLE, EVERY_DAY, EVERY_WEEK, EVERY_TWO_WEEKS, EVERY_MONTH;

    public static ClassFrequency fromShortName(String shortName) {
        switch (shortName) {
            case "S": return SINGLE;
            case "ED": return EVERY_DAY;
            case "EW": return EVERY_WEEK;
            case "ETW": return EVERY_TWO_WEEKS;
            case "EM": return EVERY_MONTH;
            default: throw new IllegalStateException();
        }
    }

    public Optional<ZonedDateTime> getNextDate(ZonedDateTime dateTime) {
        switch (this) {
            case SINGLE: return Optional.empty();
            case EVERY_DAY: return Optional.of(dateTime.plusDays(1));
            case EVERY_WEEK: return Optional.of(dateTime.plusWeeks(1));
            case EVERY_TWO_WEEKS: return Optional.of(dateTime.plusWeeks(2));
            case EVERY_MONTH: return Optional.of(dateTime.plusMonths(1));
            default: throw new IllegalStateException();
        }
    }
}
