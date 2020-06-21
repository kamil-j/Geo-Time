package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.web.rest.util.TimetableBookingsUtil;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class TimetableBookingVM implements Serializable {

    private Long id;
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private Boolean onlySemesterHalf;
    private WeekType weekType;
    private Boolean locked;
    private String type;
    private Long roomId;
    private String roomName;
    private String lecturerName;
    private String frequency;
    private Long academicUnitId;
    private String academicUnitName;
    private String academicUnitGroup;
    private String color;
    private Boolean editable;
    private Long lecturerId;
    private Set<AcademicUnitGroupDTO> relatedAcademicUnitGroups;
    private Set<Long> relatedUserIds;
    private Long classUnitGroupId;

    public TimetableBookingVM(BookingUnit bookingUnit) {
        ClassUnit classUnit = bookingUnit.getClassUnit();

        this.id = classUnit.getId();
        this.title = classUnit.getTitle();
        this.start = TimetableBookingsUtil.convertToDateTime(bookingUnit.getStartTime().toLocalTime(), bookingUnit.getDay());
        this.end = TimetableBookingsUtil.convertToDateTime(bookingUnit.getEndTime().toLocalTime(), bookingUnit.getDay());
        this.onlySemesterHalf = classUnit.isOnlySemesterHalf();
        this.locked = bookingUnit.isLocked();
        this.weekType = bookingUnit.getWeek();
        this.type = classUnit.getClassType().getName();

        Room room = bookingUnit.getRoom();
        this.roomId = room.getId();
        this.roomName = room.getName();

        User user = classUnit.getUserExt().getUser();
        this.lecturerName = user.getFirstName() + " " + user.getLastName();
        this.frequency = classUnit.getFrequency().name();
        this.academicUnitId = classUnit.getAcademicUnit().getId();
        this.academicUnitName = classUnit.getAcademicUnit().getName();

        if(classUnit.getAcademicUnitGroup() != null) {
            this.academicUnitGroup = classUnit.getAcademicUnitGroup().name();
        }
        this.color = classUnit.getClassType().getColor();
        this.lecturerId = user.getId();

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        if(classUnitGroup != null) {
            this.classUnitGroupId = classUnitGroup.getId();
        }

        if(locked != null && locked) {
            this.editable = false;
            setColor("#dc3545"); // RED
        }
    }
}
