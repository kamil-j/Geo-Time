package pl.edu.agh.geotime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the BookingUnit entity.
 */
@Repository
public interface BookingUnitRepository extends JpaRepository<BookingUnit, Long>, JpaSpecificationExecutor<BookingUnit> {
    Optional<BookingUnit> findByIdAndClassUnit_Subdepartment_Department_Id(Long id, Long departmentId);
    Optional<BookingUnit> findByIdAndClassUnit_Subdepartment_Id(Long id, Long subdepartmentId);
    Optional<BookingUnit> findByClassUnit_IdAndClassUnit_UserExt(Long id, UserExt userExt);
    List<BookingUnit> findByClassUnit_ClassUnitGroup_Id(Long id);
    void deleteByClassUnit_IdAndClassUnit_UserExt(Long id, UserExt userExt);

    @Query("SELECT count(bu.id) FROM BookingUnit bu " +
        "WHERE ((:bookingUnitId IS NULL) OR (bu.id <> :bookingUnitId)) " +
        "AND (bu.day = :day AND bu.week = :week AND (bu.classUnit.onlySemesterHalf = false OR bu.semesterHalf = :semesterHalf) " +
        "AND bu.startTime < :endTime AND bu.endTime > :startTime)" +
        "AND ((:classUnitGroup IS NULL) OR (bu.classUnit.classUnitGroup IS NULL) OR (bu.classUnit.classUnitGroup <> :classUnitGroup))" +
        "AND ((bu.room = :room) " +
        "OR (bu.classUnit.userExt = :user) " +
        "OR ((bu.classUnit.academicUnit = :academicUnit) " +
        "AND ((:academicUnitGroup IS NULL) OR (bu.classUnit.academicUnitGroup IS NULL) OR (bu.classUnit.academicUnitGroup = :academicUnitGroup))))")
    long getConflictCount(@Param("bookingUnitId") Long bookingUnitId,
                          @Param("startTime") ZonedDateTime startTime,
                          @Param("endTime") ZonedDateTime endTime,
                          @Param("day") DayOfWeek day,
                          @Param("week") WeekType week,
                          @Param("semesterHalf") SemesterHalf semesterHalf,
                          @Param("classUnitGroup") ClassUnitGroup classUnitGroup,
                          @Param("room") Room room,
                          @Param("user") UserExt user,
                          @Param("academicUnit") AcademicUnit academicUnit,
                          @Param("academicUnitGroup") AcademicUnitGroup academicUnitGroup);
}
