package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Room;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Room entity.
 */
@Repository
@Transactional
public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findAllByLocation_Department_Id(Pageable pageable, Long departmentId);
    Optional<Room> findByIdAndLocation_Department_Id(Long id, Long departmentId);
    List<Room> findByIdInAndLocation_Department_Id(Set<Long> id, Long departmentId);
    Optional<Room> findByNameAndLocation_Department_Id(String name, Long departmentId);
    void deleteByIdAndLocation_Department_Id(Long id, Long departmentId);
}
