package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassType;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ClassType entity.
 */
@Repository
@Transactional
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
    Optional<ClassType> findById(Long id);
    Optional<ClassType> findByShortName(String shortName);
}
