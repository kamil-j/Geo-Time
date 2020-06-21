package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class ClassUnitDTO implements Serializable {

    private Long id;

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

    private Boolean onlySemesterHalf;

    @NotNull
    private Long classTypeId;

    private String classTypeName;

    private Long userId;

    private String userLogin;

    @NotEmpty
    private Set<RoomDTO> rooms = new HashSet<>();

    @NotNull
    private Long academicUnitId;

    private String academicUnitName;

    @NotNull
    private Long semesterId;

    private String semesterName;

    private Long classUnitGroupId;

    private String classUnitGroupName;

    private Long subdepartmentId;

    private String subdepartmentShortName;
}
