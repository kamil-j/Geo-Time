package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.agh.geotime.domain.Subdepartment;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Subdepartment entity.
 */
@Repository
public interface SubdepartmentRepository extends JpaRepository<Subdepartment, Long> {
    Optional<Subdepartment> findByShortNameAndDepartmentId(String shortName, Long departmentId);
    Page<Subdepartment> findAllById(Pageable pageable, Long id);
    Page<Subdepartment> findAllByDepartmentId(Pageable pageable, Long departmentId);
    Optional<Subdepartment> findByIdAndDepartmentId(Long id, Long departmentId);
    void deleteByIdAndDepartmentId(Long id, Long departmentId);
}
