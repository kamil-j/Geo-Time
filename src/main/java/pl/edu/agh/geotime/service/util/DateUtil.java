package pl.edu.agh.geotime.service.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static pl.edu.agh.geotime.config.Constants.SYSTEM_EPOCH_DAY;

public class DateUtil {

    private DateUtil() {}

    public static ZonedDateTime convertToZonedDateTime(String date) {
        if(date == null) {
            return null;
        }
        DateTimeFormatter dateFormatter = getDateFormatter();
        return LocalDateTime.parse(date, dateFormatter).atZone(ZoneId.systemDefault());
    }

    private static DateTimeFormatter getDateFormatter(){
        return new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME)
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter();
    }

    public static ZonedDateTime convertToZonedDateTime(LocalTime localTime) {
        if(localTime == null) {
            return null;
        }
        return ZonedDateTime.of(SYSTEM_EPOCH_DAY, localTime, ZoneId.systemDefault());
    }

    public static ZonedDateTime convertToZonedDateTime(LocalDate localDate, LocalTime localTime) {
        if(localTime == null) {
            return null;
        }
        return ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault());
    }
}
