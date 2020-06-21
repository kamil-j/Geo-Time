package pl.edu.agh.geotime.web.rest.util;

import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.service.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class TimetableBookingsUtil {
    private TimetableBookingsUtil() {
    }

    public static ZonedDateTime convertToDateTime(LocalTime localTime, DayOfWeek dayOfWeek) {
        LocalDate firstDateSinceEpochDay = dayOfWeek.getFirstDateSinceEpochDay();
        return DateUtil.convertToZonedDateTime(firstDateSinceEpochDay, localTime);
    }
}
