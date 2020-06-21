package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.UserGroup;

import java.util.Optional;

/**
 * Spring Data JPA repository for the UserGroup entity.
 */
@Repository
@Transactional
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findById(Long id);
    Optional<UserGroup> findByName(String name);
}
