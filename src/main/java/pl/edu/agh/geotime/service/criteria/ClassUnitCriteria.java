package pl.edu.agh.geotime.service.criteria;

import java.io.Serializable;

import io.github.jhipster.service.filter.*;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

/**
 * Criteria class for the ClassUnit entity. This class is used in ClassUnitResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /class-units?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClassUnitCriteria implements Serializable {
    /**
     * Class for filtering ClassFrequency
     */
    public static class ClassFrequencyFilter extends Filter<ClassFrequency> {
    }

    /**
     * Class for filtering AcademicUnitGroup
     */
    public static class AcademicUnitGroupFilter extends Filter<AcademicUnitGroup> {
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter title;
    private StringFilter description;
    private IntegerFilter duration;
    private IntegerFilter hoursQuantity;
    private ClassFrequencyFilter frequency;
    private AcademicUnitGroupFilter academicUnitGroup;
    private BooleanFilter onlySemesterHalf;
    private LongFilter classTypeId;
    private LongFilter userExtId;
    private LongFilter roomId;
    private LongFilter bookingUnitId;
    private LongFilter scheduleUnitId;
    private LongFilter semesterId;
    private LongFilter academicUnitId;
    private LongFilter classUnitGroupId;
    private LongFilter subdepartmentId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getDuration() {
        return duration;
    }

    public void setDuration(IntegerFilter duration) {
        this.duration = duration;
    }

    public IntegerFilter getHoursQuantity() {
        return hoursQuantity;
    }

    public void setHoursQuantity(IntegerFilter hoursQuantity) {
        this.hoursQuantity = hoursQuantity;
    }

    public ClassFrequencyFilter getFrequency() {
        return frequency;
    }

    public void setFrequency(ClassFrequencyFilter frequency) {
        this.frequency = frequency;
    }

    public AcademicUnitGroupFilter getAcademicUnitGroup() {
        return academicUnitGroup;
    }

    public void setAcademicUnitGroup(AcademicUnitGroupFilter academicUnitGroup) {
        this.academicUnitGroup = academicUnitGroup;
    }

    public BooleanFilter getOnlySemesterHalf() {
        return onlySemesterHalf;
    }

    public void setOnlySemesterHalf(BooleanFilter onlySemesterHalf) {
        this.onlySemesterHalf = onlySemesterHalf;
    }

    public LongFilter getClassTypeId() {
        return classTypeId;
    }

    public void setClassTypeId(LongFilter classTypeId) {
        this.classTypeId = classTypeId;
    }

    public LongFilter getUserExtId() {
        return userExtId;
    }

    public void setUserExtId(LongFilter userExtId) {
        this.userExtId = userExtId;
    }

    public LongFilter getRoomId() {
        return roomId;
    }

    public void setRoomId(LongFilter roomId) {
        this.roomId = roomId;
    }

    public LongFilter getBookingUnitId() {
        return bookingUnitId;
    }

    public void setBookingUnitId(LongFilter bookingUnitId) {
        this.bookingUnitId = bookingUnitId;
    }

    public LongFilter getScheduleUnitId() {
        return scheduleUnitId;
    }

    public void setScheduleUnitId(LongFilter scheduleUnitId) {
        this.scheduleUnitId = scheduleUnitId;
    }

    public LongFilter getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(LongFilter semesterId) {
        this.semesterId = semesterId;
    }

    public LongFilter getAcademicUnitId() {
        return academicUnitId;
    }

    public void setAcademicUnitId(LongFilter academicUnitId) {
        this.academicUnitId = academicUnitId;
    }

    public LongFilter getClassUnitGroupId() {
        return classUnitGroupId;
    }

    public void setClassUnitGroupId(LongFilter classUnitGroupId) {
        this.classUnitGroupId = classUnitGroupId;
    }

    public LongFilter getSubdepartmentId() {
        return subdepartmentId;
    }

    public void setSubdepartmentId(LongFilter subdepartmentId) {
        this.subdepartmentId = subdepartmentId;
    }

    @Override
    public String toString() {
        return "ClassUnitCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (duration != null ? "duration=" + duration + ", " : "") +
                (hoursQuantity != null ? "quantity=" + hoursQuantity + ", " : "") +
                (frequency != null ? "frequency=" + frequency + ", " : "") +
                (academicUnitGroup != null ? "academicUnitGroup=" + academicUnitGroup + ", " : "") +
                (onlySemesterHalf != null ? "onlySemesterHalf=" + onlySemesterHalf + ", " : "") +
                (classTypeId != null ? "classTypeId=" + classTypeId + ", " : "") +
                (userExtId != null ? "userExtId=" + userExtId + ", " : "") +
                (roomId != null ? "roomId=" + roomId + ", " : "") +
                (bookingUnitId != null ? "bookingUnitId=" + bookingUnitId + ", " : "") +
                (scheduleUnitId != null ? "scheduleUnitId=" + scheduleUnitId + ", " : "") +
                (semesterId != null ? "semesterId=" + semesterId + ", " : "") +
                (academicUnitId != null ? "academicUnitId=" + academicUnitId + ", " : "") +
                (classUnitGroupId != null ? "classUnitGroupId=" + classUnitGroupId + ", " : "") +
                (subdepartmentId != null ? "subdepartmentId=" + subdepartmentId + ", " : "") +
            "}";
    }
}
