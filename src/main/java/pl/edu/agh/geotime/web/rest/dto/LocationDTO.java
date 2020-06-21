package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class LocationDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String description;

    private Long departmentId;

    private String departmentShortName;
}
