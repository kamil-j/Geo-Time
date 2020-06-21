package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class SchedulingTimeFrameDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime startTime;

    @NotNull
    private ZonedDateTime endTime;

    private Long userGroupId;

    private String userGroupName;

    private Long subdepartmentId;

    private String subdepartmentShortName;

    private Long semesterId;

    private String semesterName;
}
