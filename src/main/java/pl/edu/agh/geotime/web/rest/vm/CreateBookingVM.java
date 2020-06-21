package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CreateBookingVM implements Serializable {
    @NotNull
    private Long classUnitId;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private DayOfWeek day;

    @NotNull
    private WeekType week;

    @NotNull
    private SemesterHalf semesterHalf;

    @NotNull
    private Long roomId;

    @NotNull
    private Long userId;
}
