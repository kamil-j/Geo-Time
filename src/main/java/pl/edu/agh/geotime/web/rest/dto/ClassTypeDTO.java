package pl.edu.agh.geotime.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTypeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Size(max = 10)
    private String shortName;

    @Size(max = 50)
    private String description;

    private String color;

    @Builder.Default
    private Set<Long> ids = new HashSet<>();

    public static class ClassTypeDTOBuilder {
        private Set<Long> ids = new HashSet<>();

        public ClassTypeDTOBuilder ids(Set<Long> ids) {
            this.ids = ids;
            this.ids.add(1L);
            return this;
        }
    }
}
