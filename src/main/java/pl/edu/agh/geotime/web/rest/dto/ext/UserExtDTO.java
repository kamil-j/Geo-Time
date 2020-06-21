package pl.edu.agh.geotime.web.rest.dto.ext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.web.rest.dto.UserDTO;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class UserExtDTO extends UserDTO {

    @NotNull
    private Long subdepartmentId;

    private String subdepartmentShortName;

    @NotNull
    private Long userGroupId;

    private String userGroupName;
}
