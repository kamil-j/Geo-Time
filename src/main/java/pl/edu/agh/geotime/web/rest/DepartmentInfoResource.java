package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.service.DepartmentService;
import pl.edu.agh.geotime.web.rest.dto.DepartmentInfoDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments-info")
public class DepartmentInfoResource {

    private final Logger log = LoggerFactory.getLogger(DepartmentInfoResource.class);
    private final DepartmentService departmentService;

    DepartmentInfoResource(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<DepartmentInfoDTO>> getInfoAboutAllDepartments() {
        log.debug("REST request to get info about all departments");
        List<DepartmentInfoDTO> allDepartmentsInfo = departmentService.findAllNotFunctional()
            .stream()
            .map(DepartmentInfoDTO::new)
            .collect(Collectors.toList());
        return new ResponseEntity<>(allDepartmentsInfo, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<DepartmentInfoDTO> getInfoAboutDepartment(@PathVariable Long id) {
        log.debug("REST request to get departmentInfo : {}", id);
        Optional<DepartmentInfoDTO> departmentInfo = departmentService.findNotFunctionalById(id)
            .map(DepartmentInfoDTO::new);
        return ResponseUtil.wrapOrNotFound(departmentInfo);
    }
}
