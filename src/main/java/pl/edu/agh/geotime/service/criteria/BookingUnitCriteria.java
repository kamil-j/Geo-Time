package pl.edu.agh.geotime.service.criteria;

import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.filter.LocalTimeFilter;
import pl.edu.agh.geotime.service.util.FilterUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Criteria class for the BookingUnit entity. This class is used in BookingUnitResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /booking-units?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BookingUnitCriteria implements Serializable {
    /**
     * Class for filtering DayOfWeek
     */
    public static class DayOfWeekFilter extends Filter<DayOfWeek> {
    }

    /**
     * Class for filtering WeekType
     */
    public static class WeekTypeFilter extends Filter<WeekType> {
    }

    /**
     * Class for filtering SemesterHalf
     */
    public static class SemesterHalfFilter extends Filter<SemesterHalf> {
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private LocalTimeFilter startTime;
    private LocalTimeFilter endTime;
    private DayOfWeekFilter day;
    private WeekTypeFilter week;
    private SemesterHalfFilter semesterHalf;
    private BooleanFilter locked;
    private LongFilter classUnitId;
    private LongFilter roomId;
    private LongFilter subdepartmentId;
    private Long semesterIdEquals;
    private Long academicUnitIdEquals;
    private Long userIdNotEquals;
    private Long userIdEquals;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalTimeFilter getStartTime() {
        return startTime;
    }

    public ZonedDateTimeFilter getStartDateTime() {
        return FilterUtil.convertToZonedDateTimeFiler(startTime);
    }

    public void setStartTime(LocalTimeFilter startTime) {
        this.startTime = startTime;
    }

    public LocalTimeFilter getEndTime() {
        return endTime;
    }

    public ZonedDateTimeFilter getEndDateTime() {
        return FilterUtil.convertToZonedDateTimeFiler(endTime);
    }

    public void setEndTime(LocalTimeFilter endTime) {
        this.endTime = endTime;
    }

    public DayOfWeekFilter getDay() {
        return day;
    }

    public void setDay(DayOfWeekFilter day) {
        this.day = day;
    }

    public WeekTypeFilter getWeek() {
        return week;
    }

    public void setWeek(WeekTypeFilter week) {
        this.week = week;
    }

    public void setWeekTypeEquals(WeekType week) {
        if(week == null) {
            return;
        }
        WeekTypeFilter weekTypeFilter = new WeekTypeFilter();
        weekTypeFilter.setEquals(week);
        this.week = weekTypeFilter;
    }

    public SemesterHalfFilter getSemesterHalf() {
        return semesterHalf;
    }

    public void setSemesterHalf(SemesterHalfFilter semesterHalf) {
        this.semesterHalf = semesterHalf;
    }

    public void setSemesterHalfEquals(SemesterHalf semesterHalf) {
        if(semesterHalf == null) {
            return;
        }
        SemesterHalfFilter semesterHalfFilter = new SemesterHalfFilter();
        semesterHalfFilter.setEquals(semesterHalf);
        this.semesterHalf = semesterHalfFilter;
    }

    public BooleanFilter getLocked() {
        return locked;
    }

    public void setLocked(BooleanFilter locked) {
        this.locked = locked;
    }

    public LongFilter getClassUnitId() {
        return classUnitId;
    }

    public void setClassUnitId(LongFilter classUnitId) {
        this.classUnitId = classUnitId;
    }

    public LongFilter getRoomId() {
        return roomId;
    }

    public void setRoomId(LongFilter roomId) {
        this.roomId = roomId;
    }

    public void setRoomIdIn(List<Long> roomIds) {
        if(roomIds != null && !roomIds.isEmpty()) {
            LongFilter filter = new LongFilter();
            filter.setIn(roomIds);
            this.setRoomId(filter);
        }
    }

    public LongFilter getSubdepartmentId() {
        return subdepartmentId;
    }

    public void setSubdepartmentId(LongFilter subdepartmentId) {
        this.subdepartmentId = subdepartmentId;
    }

    public Long getSemesterIdEquals() {
        return semesterIdEquals;
    }

    public void setSemesterIdEquals(Long semesterIdEquals) {
        this.semesterIdEquals = semesterIdEquals;
    }

    public Long getAcademicUnitIdEquals() {
        return academicUnitIdEquals;
    }

    public void setAcademicUnitIdEquals(Long academicUnitIdEquals) {
        this.academicUnitIdEquals = academicUnitIdEquals;
    }

    public Long getUserIdNotEquals() {
        return userIdNotEquals;
    }

    public void setUserIdNotEquals(Long userIdNotEquals) {
        this.userIdNotEquals = userIdNotEquals;
    }

    public Long getUserIdEquals() {
        return userIdEquals;
    }

    public void setUserIdEquals(Long userIdEquals) {
        this.userIdEquals = userIdEquals;
    }

    @Override
    public String toString() {
        return "BookingUnitCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startTime != null ? "startTime=" + startTime + ", " : "") +
                (endTime != null ? "endTime=" + endTime + ", " : "") +
                (day != null ? "day=" + day + ", " : "") +
                (week != null ? "week=" + week + ", " : "") +
                (semesterHalf != null ? "semesterHalf=" + semesterHalf + ", " : "") +
                (locked != null ? "locked=" + locked + ", " : "") +
                (classUnitId != null ? "classUnitId=" + classUnitId + ", " : "") +
                (roomId != null ? "roomId=" + roomId + ", " : "") +
                (subdepartmentId != null ? "subdepartmentId=" + subdepartmentId + ", " : "") +
                (semesterIdEquals != null ? "semesterIdEquals=" + semesterIdEquals + ", " : "") +
                (academicUnitIdEquals != null ? "academicUnitIdEquals=" + academicUnitIdEquals + ", " : "") +
                (userIdNotEquals != null ? "userIdNotEquals=" + userIdNotEquals + ", " : "") +
                (userIdEquals != null ? "userIdEquals=" + userIdEquals + ", " : "") +
            "}";
    }
}
