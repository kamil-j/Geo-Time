package pl.edu.agh.geotime.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class LockBookingVM implements Serializable {
    @NotNull
    private Long classUnitId;

    @NotNull
    private Long userId;

    private Boolean lock;
}
