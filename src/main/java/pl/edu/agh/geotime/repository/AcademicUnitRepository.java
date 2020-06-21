package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;

import java.util.Optional;

@Repository
@Transactional
public interface AcademicUnitRepository extends JpaRepository<AcademicUnit, Long>, JpaSpecificationExecutor<AcademicUnit> {
    Optional<AcademicUnit> findOneById(Long id);
    Optional<AcademicUnit> findByStudyField_IdAndYearAndDegree(Long studyFieldId, AcademicUnitYear year, AcademicUnitDegree degree);
    Page<AcademicUnit> findAllByStudyField_Department_Id(Pageable pageable, Long departmentId);
    Optional<AcademicUnit> findByIdAndStudyField_Department_Id(Long id, Long departmentId);
    void deleteByIdAndStudyField_DepartmentId(Long id, Long departmentId);

    @EntityGraph(attributePaths = "classUnits")
    Optional<AcademicUnit> findOneWithClassUnitsById(Long id);
}
