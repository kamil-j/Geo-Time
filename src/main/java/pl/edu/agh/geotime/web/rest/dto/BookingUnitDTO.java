package pl.edu.agh.geotime.web.rest.dto;

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
public class BookingUnitDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private DayOfWeek day;

    @NotNull
    private WeekType week;

    private SemesterHalf semesterHalf;

    private Boolean locked;

    @NotNull
    private Long classUnitId;

    private String classUnitTitle;

    @NotNull
    private Long roomId;

    private String roomName;
}
