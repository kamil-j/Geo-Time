package pl.edu.agh.geotime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the ClassUnit entity.
 */
@Repository
@Transactional
public interface ClassUnitRepository extends JpaRepository<ClassUnit, Long>, JpaSpecificationExecutor<ClassUnit> {
    void deleteByIdAndSubdepartment_Department_Id(Long id, Long departmentId);
    void deleteByIdAndSubdepartment_Id(Long id, Long subdepartmentId);
    Set<ClassUnit> findByBookingUnitIsNotNullAndClassUnitGroup_IdIn(Set<Long> classUnitGroupIds);
    Set<ClassUnit> findByBookingUnitIsNullAndClassUnitGroup_Id(Long classUnitGroupId);
    Set<ClassUnit> findByBookingUnitIsNotNullAndUserExtIsNotAndClassUnitGroup_IdIn(UserExt userExt, Set<Long> classUnitGroupIds);
    Set<ClassUnit> findBySemester_Id(Long semesterId);
    Optional<ClassUnit> findByIdAndUserExt(Long id, UserExt userExt);
    Page<ClassUnit> findAllBySubdepartment_Department_Id(Pageable page, Long departmentId);
    Page<ClassUnit> findAllBySubdepartment_Id(Pageable page, Long subdepartmentId);

    @EntityGraph(attributePaths = {"rooms"})
    Page<ClassUnit> findAll(Specification specification, Pageable page);

    @EntityGraph(attributePaths = {"rooms"})
    Optional<ClassUnit> findByIdAndSubdepartment_Department_Id(Long id, Long departmentId);

    @EntityGraph(attributePaths = {"rooms"})
    Optional<ClassUnit> findByIdAndSubdepartment_Id(Long id, Long subdepartmentId);

    @EntityGraph(attributePaths = {"rooms"})
    Page<ClassUnit> findByUserExtAndSemesterAndBookingUnitIsNullOrderByTitle(Pageable pageable, UserExt userExt, Semester semester);

    @EntityGraph(attributePaths = {"rooms"})
    Optional<ClassUnit> findByIdAndUserExtAndBookingUnitNull(Long id, UserExt userExt);
}
