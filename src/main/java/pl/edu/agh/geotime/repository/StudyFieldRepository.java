package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.StudyField;

import java.util.Optional;

/**
 * Spring Data JPA repository for the StudyField entity.
 */
@Repository
@Transactional
public interface StudyFieldRepository extends JpaRepository<StudyField, Long> {
    Page<StudyField> findAllByDepartment_Id(Pageable pageable, Long departmentId);
    Optional<StudyField> findByIdAndDepartment_Id(Long id, Long departmentId);
    Optional<StudyField> findByShortNameAndDepartment_Id(String shortName, Long departmentId);
    void deleteByIdAndDepartment_Id(Long id, Long departmentId);
}
