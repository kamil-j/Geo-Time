package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserGroup;

import java.util.Optional;

/**
 * Spring Data JPA repository for the SchedulingTimeFrame entity.
 */
@Repository
@Transactional
public interface SchedulingTimeFrameRepository extends JpaRepository<SchedulingTimeFrame, Long> {
    Page<SchedulingTimeFrame> findAllBySubdepartment_Department_Id(Pageable pageable, Long departmentId);
    Optional<SchedulingTimeFrame> findByIdAndSubdepartment_Department_Id(Long id, Long departmentId);
    Optional<SchedulingTimeFrame> findByUserGroupAndSubdepartmentAndSemester(UserGroup userGroup, Subdepartment subdepartment, Semester semester);
    void deleteByIdAndSubdepartment_Department_Id(Long id, Long departmentId);
}
