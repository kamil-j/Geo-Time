package pl.edu.agh.geotime.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Semester;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Semester entity.
 */
@Repository
@Transactional
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    String ACTIVE_SEMESTER_CACHE = "activeSemester";

    Optional<Semester> findById(Long id);

    @Cacheable(cacheNames = ACTIVE_SEMESTER_CACHE)
    Optional<Semester> findOneByActiveTrue();

    Optional<Semester> getOneByActiveTrue(); // Without cache - issue related with planning metrics (LazyInitializationException)
}
