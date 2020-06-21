package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Location;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Location entity.
 */
@Repository
@Transactional
public interface LocationRepository extends JpaRepository<Location, Long> {
    Page<Location> findAllByDepartmentId(Pageable pageable, Long departmentId);
    Optional<Location> findByIdAndDepartmentId(Long id, Long departmentId);
    void deleteByIdAndDepartmentId(Long id, Long departmentId);
}
