package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.web.rest.util.TimetableBookingsUtil;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class AcademicUnitBookingConflictVM implements Serializable {

    @NotNull
    private ZonedDateTime start;

    @NotNull
    private ZonedDateTime end;

    @NotNull
    private ClassFrequency frequency;

    @NotNull
    private WeekType weekType;

    @NotNull
    private Long academicUnitId;

    private AcademicUnitGroup academicUnitGroup;

    private Long classUnitGroupId;

    public AcademicUnitBookingConflictVM(BookingUnit bookingUnit) {
        this.start = TimetableBookingsUtil.convertToDateTime(bookingUnit.getStartTime().toLocalTime(), bookingUnit.getDay());
        this.end = TimetableBookingsUtil.convertToDateTime(bookingUnit.getEndTime().toLocalTime(), bookingUnit.getDay());
        this.frequency = bookingUnit.getClassUnit().getFrequency();
        this.weekType = bookingUnit.getWeek();
        this.academicUnitId = bookingUnit.getClassUnit().getAcademicUnit().getId();
        this.academicUnitGroup = bookingUnit.getClassUnit().getAcademicUnitGroup();

        ClassUnitGroup classUnitGroup = bookingUnit.getClassUnit().getClassUnitGroup();
        if(classUnitGroup != null) {
            this.classUnitGroupId = classUnitGroup.getId();
        }
    }
}
