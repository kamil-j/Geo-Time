package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.repository.SemesterRepository;
import pl.edu.agh.geotime.service.dto.DepartmentPlanningMetricsDTO;
import pl.edu.agh.geotime.service.dto.SubdepartmentPlanningMetricsDTO;
import pl.edu.agh.geotime.service.dto.UserPlanningMetricsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class PlanningMetricsService {

    private final Logger log = LoggerFactory.getLogger(PlanningMetricsService.class);

    private final SemesterRepository semesterRepository;

    @Autowired
    public PlanningMetricsService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public SubdepartmentPlanningMetricsDTO getSubdepartmentPlanningMetrics(Subdepartment subdepartment) {
        Optional<Semester> currentSemesterOptional = semesterRepository.getOneByActiveTrue();
        if(!currentSemesterOptional.isPresent()) {
            log.info("Current semester not found. Returning empty planning metrics.");
            return null;
        }

        List<ClassUnit> classUnits = currentSemesterOptional.get().getClassUnits().stream()
            .filter(classUnit -> subdepartment.getId().equals(classUnit.getSubdepartment().getId()))
            .collect(Collectors.toList());

        return createFullSubdepartmentPlanningMetrics(subdepartment, classUnits);
    }

    private SubdepartmentPlanningMetricsDTO createFullSubdepartmentPlanningMetrics(Subdepartment subdepartment, List<ClassUnit> classUnits) {
        List<UserPlanningMetricsDTO> usersPlanningMetrics = getUsersPlanningMetrics(classUnits);
        int bookedClassUnitsQuantity = usersPlanningMetrics.stream()
            .mapToInt(UserPlanningMetricsDTO::getBookedClassQuantity)
            .sum();

        SubdepartmentPlanningMetricsDTO subdepartmentPlanningMetricsDTO = new SubdepartmentPlanningMetricsDTO();
        subdepartmentPlanningMetricsDTO.setSubdepartmentId(subdepartment.getId());
        subdepartmentPlanningMetricsDTO.setSubdepartmentShortName(subdepartment.getShortName());
        subdepartmentPlanningMetricsDTO.setClassQuantity(classUnits.size());
        subdepartmentPlanningMetricsDTO.setBookedClassQuantity(bookedClassUnitsQuantity);
        subdepartmentPlanningMetricsDTO.setUsersPlanningMetrics(usersPlanningMetrics);

        return subdepartmentPlanningMetricsDTO;
    }

    private List<UserPlanningMetricsDTO> getUsersPlanningMetrics(List<ClassUnit> classUnits) {
        List<UserPlanningMetricsDTO> usersPlanningMetrics = new ArrayList<>();

        classUnits.stream()
            .filter(classUnit -> classUnit.getUserExt() != null)
            .collect(groupingBy(ClassUnit::getUserExt))
            .forEach((userExt, userClassUnits) -> {
                int bookedUserClassUnitsQuantity = (int) userClassUnits.stream()
                    .filter(classUnit -> classUnit.getBookingUnit() != null)
                    .count();

                UserPlanningMetricsDTO userMetrics = new UserPlanningMetricsDTO();
                userMetrics.setUserName(userExt.getUser().getLogin());
                userMetrics.setGroupName(userExt.getUserGroup().getName());
                userMetrics.setClassQuantity(userClassUnits.size());
                userMetrics.setBookedClassQuantity(bookedUserClassUnitsQuantity);
                usersPlanningMetrics.add(userMetrics);
            });

        return usersPlanningMetrics;
    }

    public DepartmentPlanningMetricsDTO getDepartmentPlanningMetrics(Department department) {
        Optional<Semester> currentSemesterOptional = semesterRepository.getOneByActiveTrue();
        if(!currentSemesterOptional.isPresent()) {
            log.info("Current semester not found. Returning empty planning metrics.");
            return null;
        }

        Map<Subdepartment, List<ClassUnit>> subdepartmentsClassUnits = currentSemesterOptional.get().getClassUnits().stream()
            .filter(classUnit -> department.equals(classUnit.getSubdepartment().getDepartment()))
            .collect(groupingBy(ClassUnit::getSubdepartment));

        return createDepartmentPlanningMetrics(department, subdepartmentsClassUnits);
    }

    private DepartmentPlanningMetricsDTO createDepartmentPlanningMetrics(Department department,
                                                                         Map<Subdepartment, List<ClassUnit>> subdepartmentsClassUnits) {
        int departmentClassQuantity = 0;
        int departmentBookedClassQuantity = 0;

        List<SubdepartmentPlanningMetricsDTO> subdepartmentsPlanningMetrics = new ArrayList<>();
        for(Map.Entry<Subdepartment, List<ClassUnit>> subdepartmentClassUnits : subdepartmentsClassUnits.entrySet()) {
            Subdepartment subdepartment = subdepartmentClassUnits.getKey();
            List<ClassUnit> classUnits = subdepartmentClassUnits.getValue();

            SubdepartmentPlanningMetricsDTO subdepartmentPlanningMetricsDTO = createSubdepartmentPlanningMetrics(subdepartment,
                classUnits);
            subdepartmentsPlanningMetrics.add(subdepartmentPlanningMetricsDTO);

            departmentClassQuantity += classUnits.size();
            departmentBookedClassQuantity += subdepartmentPlanningMetricsDTO.getBookedClassQuantity();
        }

        DepartmentPlanningMetricsDTO planningMetricsDTO = new DepartmentPlanningMetricsDTO();
        planningMetricsDTO.setDepartmentId(department.getId());
        planningMetricsDTO.setDepartmentShortName(department.getShortName());
        planningMetricsDTO.setClassQuantity(departmentClassQuantity);
        planningMetricsDTO.setBookedClassQuantity(departmentBookedClassQuantity);
        planningMetricsDTO.setSubdepartmentsPlanningMetrics(subdepartmentsPlanningMetrics);

        return planningMetricsDTO;
    }

    private SubdepartmentPlanningMetricsDTO createSubdepartmentPlanningMetrics(Subdepartment subdepartment, List<ClassUnit> classUnits) {
        SubdepartmentPlanningMetricsDTO subdepartmentPlanningMetricsDTO = new SubdepartmentPlanningMetricsDTO();
        subdepartmentPlanningMetricsDTO.setSubdepartmentId(subdepartment.getId());
        subdepartmentPlanningMetricsDTO.setSubdepartmentShortName(subdepartment.getShortName());
        subdepartmentPlanningMetricsDTO.setClassQuantity(classUnits.size());
        subdepartmentPlanningMetricsDTO.setBookedClassQuantity(getBookedClassUnitsQuantity(classUnits));

        return subdepartmentPlanningMetricsDTO;
    }

    private int getBookedClassUnitsQuantity(List<ClassUnit> classUnits) {
        return (int) classUnits.stream()
            .filter(classUnit -> classUnit.getBookingUnit() != null)
            .count();
    }
}
