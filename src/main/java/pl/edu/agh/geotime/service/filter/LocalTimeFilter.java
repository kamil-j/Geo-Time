package pl.edu.agh.geotime.service.filter;

import io.github.jhipster.service.filter.RangeFilter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public class LocalTimeFilter extends RangeFilter<LocalTime> {

    private static final long serialVersionUID = 1L;

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalTimeFilter setEquals(LocalTime equals) {
        super.setEquals(equals);
        return this;
    }

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalTimeFilter setGreaterThan(LocalTime equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalTimeFilter setGreaterOrEqualThan(LocalTime equals) {
        super.setGreaterOrEqualThan(equals);
        return this;
    }

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalTimeFilter setLessThan(LocalTime equals) {
        super.setLessThan(equals);
        return this;
    }

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalTimeFilter setLessOrEqualThan(LocalTime equals) {
        super.setLessOrEqualThan(equals);
        return this;
    }
}
