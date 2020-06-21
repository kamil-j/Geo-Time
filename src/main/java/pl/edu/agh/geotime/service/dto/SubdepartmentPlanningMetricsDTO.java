package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SubdepartmentPlanningMetricsDTO implements Serializable {
    private Long subdepartmentId;
    private String subdepartmentShortName;
    private int classQuantity;
    private int bookedClassQuantity;
    private List<UserPlanningMetricsDTO> usersPlanningMetrics;
}
