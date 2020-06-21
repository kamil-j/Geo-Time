package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Room;
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.domain.User;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ScheduleUnitInfoDTO implements Serializable {
    private Long id;
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private String type;
    private Long roomId;
    private String roomName;
    private String lecturerName;
    private String frequency;
    private Long academicUnitId;
    private String academicUnitGroup;
    private String color;

    public ScheduleUnitInfoDTO(ScheduleUnit scheduleUnit) {
        ClassUnit classUnit = scheduleUnit.getClassUnit();

        this.id = classUnit.getId();
        this.title = classUnit.getTitle();
        this.start = scheduleUnit.getStartDate();
        this.end = scheduleUnit.getEndDate();
        this.type = classUnit.getClassType().getName();

        Room room = scheduleUnit.getRoom();
        this.roomId = room.getId();
        this.roomName = room.getName();

        User user = classUnit.getUserExt().getUser();
        this.lecturerName = user.getFirstName() + " " + user.getLastName();
        this.frequency = classUnit.getFrequency().name();
        this.academicUnitId = classUnit.getAcademicUnit().getId();

        if(classUnit.getAcademicUnitGroup() != null) {
            this.academicUnitGroup = classUnit.getAcademicUnitGroup().name();
        }
        this.color = classUnit.getClassType().getColor();
    }
}
