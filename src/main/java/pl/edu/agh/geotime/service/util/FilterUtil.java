package pl.edu.agh.geotime.service.util;

import io.github.jhipster.service.filter.ZonedDateTimeFilter;
import pl.edu.agh.geotime.service.filter.LocalTimeFilter;

public class FilterUtil {
    private FilterUtil() {
    }

    public static ZonedDateTimeFilter convertToZonedDateTimeFiler(LocalTimeFilter localTimeFilter) {
        ZonedDateTimeFilter zonedDateTimeFilter = new ZonedDateTimeFilter();
        zonedDateTimeFilter.setLessOrEqualThan(DateUtil.convertToZonedDateTime(localTimeFilter.getLessOrEqualThan()));
        zonedDateTimeFilter.setGreaterOrEqualThan(DateUtil.convertToZonedDateTime(localTimeFilter.getGreaterOrEqualThan()));
        zonedDateTimeFilter.setEquals(DateUtil.convertToZonedDateTime(localTimeFilter.getEquals()));
        zonedDateTimeFilter.setGreaterThan(DateUtil.convertToZonedDateTime(localTimeFilter.getGreaterThan()));
        zonedDateTimeFilter.setLessThan(DateUtil.convertToZonedDateTime(localTimeFilter.getLessThan()));
        zonedDateTimeFilter.setSpecified(localTimeFilter.getSpecified());

        return zonedDateTimeFilter;
    }
}
