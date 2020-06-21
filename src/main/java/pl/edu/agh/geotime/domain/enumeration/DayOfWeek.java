package pl.edu.agh.geotime.domain.enumeration;

import java.time.LocalDate;

import static pl.edu.agh.geotime.config.Constants.SYSTEM_EPOCH_DAY;

public enum DayOfWeek {
    MON(0), TUE(1), WED(2), THU(3), FRI(4);

    private final int dayOfWeekNumber;

    DayOfWeek(int dayOfWeekNumber) {
        this.dayOfWeekNumber = dayOfWeekNumber;
    }

    public LocalDate getFirstDateSinceEpochDay() {
        return SYSTEM_EPOCH_DAY.plusDays(dayOfWeekNumber);
    }

    public java.time.DayOfWeek toDayOfWeek() {
        switch (this) {
            case MON: return java.time.DayOfWeek.MONDAY;
            case TUE: return java.time.DayOfWeek.TUESDAY;
            case WED: return java.time.DayOfWeek.WEDNESDAY;
            case THU: return java.time.DayOfWeek.THURSDAY;
            case FRI: return java.time.DayOfWeek.FRIDAY;
            default: throw new IllegalStateException();
        }
    }
}
