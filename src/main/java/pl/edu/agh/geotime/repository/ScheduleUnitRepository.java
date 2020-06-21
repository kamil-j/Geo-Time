package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ScheduleUnit entity.
 */
@Repository
@Transactional
public interface ScheduleUnitRepository extends JpaRepository<ScheduleUnit, Long>, JpaSpecificationExecutor<ScheduleUnit> {
    Optional<ScheduleUnit> findByIdAndClassUnit_Subdepartment_Department_Id(Long id, Long departmentId);
    void deleteByIdAndClassUnit_Subdepartment_Department_Id(Long id, Long departmentId);
    void deleteByClassUnit_Id(Long classUnitId);
    void deleteByClassUnit_IdAndStartDateAfter(Long classUnitId, ZonedDateTime dateTime);

    @Query("SELECT count(su.id) FROM ScheduleUnit su " +
        "WHERE ((:scheduleUnitId IS NULL) OR (su.id <> :scheduleUnitId)) " +
        "AND (su.startDate < :endDate) AND (su.endDate > :startDate) " +
        "AND ((:classUnitGroup IS NULL) OR (su.classUnit.classUnitGroup IS NULL) OR (su.classUnit.classUnitGroup <> :classUnitGroup))" +
        "AND ((su.room = :room) " +
            "OR (su.classUnit.userExt = :user) " +
            "OR ((su.classUnit.academicUnit = :academicUnit) " +
                "AND ((:academicUnitGroup IS NULL) OR (su.classUnit.academicUnitGroup IS NULL) OR (su.classUnit.academicUnitGroup = :academicUnitGroup))" +
            ")" +
        ")")
    long getConflictCount(@Param("scheduleUnitId") Long scheduleUnitId,
                          @Param("startDate") ZonedDateTime startDate,
                          @Param("endDate") ZonedDateTime endDate,
                          @Param("classUnitGroup") ClassUnitGroup classUnitGroup,
                          @Param("room") Room room,
                          @Param("user") UserExt user,
                          @Param("academicUnit") AcademicUnit academicUnit,
                          @Param("academicUnitGroup") AcademicUnitGroup academicUnitGroup);
}
