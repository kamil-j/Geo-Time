package pl.edu.agh.geotime.service.criteria;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Criteria class for the ScheduleUnit entity. This class is used in ScheduleUnitResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /schedule-units?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ScheduleUnitCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private ZonedDateTimeFilter startDate;
    private ZonedDateTimeFilter endDate;
    private LongFilter classUnitId;
    private LongFilter roomId;
    private LongFilter subdepartmentId;
    private AcademicUnitGroup academicUnitGroupEquals;
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

    public ZonedDateTimeFilter getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTimeFilter startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTimeFilter getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTimeFilter endDate) {
        this.endDate = endDate;
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

    public LongFilter getSubdepartmentId() {
        return subdepartmentId;
    }

    public void setSubdepartmentId(LongFilter subdepartmentId) {
        this.subdepartmentId = subdepartmentId;
    }

    public AcademicUnitGroup getAcademicUnitGroupEquals() {
        return academicUnitGroupEquals;
    }

    public void setAcademicUnitGroupEquals(AcademicUnitGroup academicUnitGroupEquals) {
        this.academicUnitGroupEquals = academicUnitGroupEquals;
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

    public void setStartDateGreaterOrEqualThan(ZonedDateTime startDate) {
        if(startDate != null) {
            ZonedDateTimeFilter filter = new ZonedDateTimeFilter();
            filter.setGreaterOrEqualThan(startDate);
            this.setStartDate(filter);
        }
    }

    public void setEndDateLessOrEqualThan(ZonedDateTime endDate) {
        if(endDate != null) {
            ZonedDateTimeFilter filter = new ZonedDateTimeFilter();
            filter.setLessOrEqualThan(endDate);
            this.setEndDate(filter);
        }
    }

    public void setRoomIdIn(List<Long> roomIds) {
        if(roomIds != null && !roomIds.isEmpty()) {
            LongFilter filter = new LongFilter();
            filter.setIn(roomIds);
            this.setRoomId(filter);
        }
    }

    @Override
    public String toString() {
        return "ScheduleUnitCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startDate != null ? "startDate=" + startDate + ", " : "") +
                (endDate != null ? "endDate=" + endDate + ", " : "") +
                (classUnitId != null ? "classUnitId=" + classUnitId + ", " : "") +
                (roomId != null ? "roomId=" + roomId + ", " : "") +
                (subdepartmentId != null ? "subdepartmentId=" + subdepartmentId + ", " : "") +
                (academicUnitGroupEquals != null ? "academicUnitGroupEquals=" + academicUnitGroupEquals + ", " : "") +
                (semesterIdEquals != null ? "semesterIdEquals=" + semesterIdEquals + ", " : "") +
                (academicUnitIdEquals != null ? "academicUnitIdEquals=" + academicUnitIdEquals + ", " : "") +
                (userIdNotEquals != null ? "userIdNotEquals=" + userIdNotEquals + ", " : "") +
                (userIdEquals != null ? "userIdEquals=" + userIdEquals + ", " : "") +
            "}";
    }
}
