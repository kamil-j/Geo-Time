package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class ClassUnitGroupDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Long departmentId;

    private String departmentShortName;
}
