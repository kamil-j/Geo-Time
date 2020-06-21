package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassUnitGroup;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ClassUnitGroup entity.
 */
@Repository
@Transactional
public interface ClassUnitGroupRepository extends JpaRepository<ClassUnitGroup, Long> {
    Page<ClassUnitGroup> findAllByDepartment_Id(Pageable pageable, Long departmentId);
    Optional<ClassUnitGroup> findByIdAndDepartment_Id(Long id, Long departmentId);
    void deleteByIdAndDepartment_Id(Long id, Long departmentId);

    @EntityGraph(attributePaths = {"classUnits"})
    @Query("select c from ClassUnitGroup c WHERE c.id = :id")
    ClassUnitGroup findByIdWithClassUnits(@Param("id") Long id);
}
