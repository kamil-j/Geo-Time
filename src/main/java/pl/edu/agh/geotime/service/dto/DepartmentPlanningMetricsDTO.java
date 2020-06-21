package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class DepartmentPlanningMetricsDTO implements Serializable {
    private long departmentId;
    private String departmentShortName;
    private int classQuantity;
    private int bookedClassQuantity;
    private List<SubdepartmentPlanningMetricsDTO> subdepartmentsPlanningMetrics;
}
