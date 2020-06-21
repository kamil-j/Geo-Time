package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.Semester;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class SemesterInfoDTO implements Serializable {
    private ZonedDateTime start;
    private ZonedDateTime end;

    public SemesterInfoDTO(Semester semester) {
        this.start = semester.getStartDate();
        this.end = semester.getEndDate();
    }
}
