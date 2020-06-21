package pl.edu.agh.geotime.config;

import java.time.LocalDate;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "pl";

    public static final int TIME_SPACE_BETWEEN_SCHEDULE_UNITS = 5;
    public static final int TIME_SPACE_BETWEEN_BOOKING_UNITS = 5;
    public static final LocalDate SYSTEM_EPOCH_DAY = LocalDate.ofEpochDay(4); //First Monday since 1970-01-01

    private Constants() {
    }
}
