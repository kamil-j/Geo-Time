package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class ClassUnitCreateVM implements Serializable {
    @NotNull
    @Size(max = 80)
    private String title;

    @Size(max = 100)
    private String description;

    @NotNull
    private Integer duration;

    @NotNull
    private Integer hoursQuantity;

    @NotNull
    private ClassFrequency frequency;

    private AcademicUnitGroup academicUnitGroup;

    @NotNull
    private Long classTypeId;

    private Long userId;

    @NotEmpty
    private Set<Long> roomIds = new HashSet<>();

    @NotNull
    private Long academicUnitId;

    @NotNull
    private Long semesterId;

    private ClassUnitGroup classUnitGroup;

    private Long subdepartmentId;
}
