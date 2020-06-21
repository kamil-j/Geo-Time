package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserPlanningMetricsDTO implements Serializable {
    private String userName;
    private String groupName;
    private int classQuantity;
    private int bookedClassQuantity;
}
