package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.RoomType;

import java.util.Optional;

/**
 * Spring Data JPA repository for the RoomType entity.
 */
@Repository
@Transactional
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Optional<RoomType> findById(Long id);
}
