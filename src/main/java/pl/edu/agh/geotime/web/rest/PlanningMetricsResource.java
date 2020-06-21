package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.service.PlanningMetricsService;
import pl.edu.agh.geotime.service.SubdepartmentService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.service.dto.DepartmentPlanningMetricsDTO;
import pl.edu.agh.geotime.service.dto.SubdepartmentPlanningMetricsDTO;
import pl.edu.agh.geotime.service.util.UserUtil;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.errors.InternalServerErrorException;

/**
 * REST controller for planning metrics.
 */
@RestController
@RequestMapping("/api/planning-metrics")
public class PlanningMetricsResource {

    private final Logger log = LoggerFactory.getLogger(PlanningMetricsResource.class);
    private final PlanningMetricsService planningMetricsService;
    private final SubdepartmentService subdepartmentService;
    private final UserService userService;

    PlanningMetricsResource(PlanningMetricsService planningMetricsService, SubdepartmentService subdepartmentService,
                            UserService userService) {
        this.planningMetricsService = planningMetricsService;
        this.subdepartmentService = subdepartmentService;
        this.userService = userService;
    }

    private UserExt getCurrentUser() {
        return userService.getUserWithAuthorities().orElseThrow(
            () -> new InternalServerErrorException("Current user not found"));
    }

    private Department getCurrentUserDepartment() {
        return getCurrentUser()
            .getSubdepartment()
            .getDepartment();
    }

    @GetMapping("/department")
    @Timed
    public ResponseEntity<DepartmentPlanningMetricsDTO> getDepartmentPlanningMetrics() {
        log.debug("REST request to get department planning metrics");
        Department department = getCurrentUserDepartment();

        DepartmentPlanningMetricsDTO planningMetricsDTO = null;
        if(!department.isFunctional()) {
            planningMetricsDTO = planningMetricsService.getDepartmentPlanningMetrics(department);
        }

        return new ResponseEntity<>(planningMetricsDTO, HttpStatus.OK);
    }

    @GetMapping({"/subdepartment", "/subdepartment/{subdepartmentId}"})
    @Timed
    public ResponseEntity<SubdepartmentPlanningMetricsDTO> getSubdepartmentPlanningMetrics(@PathVariable(required = false) Long subdepartmentId) {
        log.debug("REST request to get subdepartment planning metrics");
        UserExt user = getCurrentUser();

        Subdepartment subdepartment;
        if(UserUtil.isManager(user) && subdepartmentId != null) {
            subdepartment = subdepartmentService.findById(subdepartmentId)
                .orElseThrow(() -> new BadRequestAlertException("Cannot find subdepartment with id: " + subdepartmentId,
                "subdepartment", "notfound"));
        } else {
            subdepartment = new Subdepartment();
            subdepartment.setId(user.getSubdepartment().getId());
            subdepartment.setShortName(user.getSubdepartment().getShortName());
        }

        SubdepartmentPlanningMetricsDTO planningMetricsDTO = planningMetricsService.getSubdepartmentPlanningMetrics(subdepartment);

        return new ResponseEntity<>(planningMetricsDTO, HttpStatus.OK);
    }
}
