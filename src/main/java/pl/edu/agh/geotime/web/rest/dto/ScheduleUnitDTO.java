package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ScheduleUnitDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime startDate;

    @NotNull
    private ZonedDateTime endDate;

    @NotNull
    private Long classUnitId;

    private String classUnitTitle;

    @NotNull
    private Long roomId;

    private String roomName;
}
