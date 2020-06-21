package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.repository.BookingUnitRepository;
import pl.edu.agh.geotime.service.errors.BookingConflictException;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.edu.agh.geotime.config.Constants.TIME_SPACE_BETWEEN_BOOKING_UNITS;

@Service
@Transactional
public class BookingUnitService {

    private static final Logger log = LoggerFactory.getLogger(BookingUnitService.class);

    private final DepartmentAccessService departmentAccessService;
    private final BookingUnitRepository bookingUnitRepository;
    private final ScheduleService scheduleService;
    private final UserHelper userHelper;

    public BookingUnitService(DepartmentAccessService departmentAccessService, BookingUnitRepository bookingUnitRepository,
                              ScheduleService scheduleService, UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.bookingUnitRepository = bookingUnitRepository;
        this.scheduleService = scheduleService;
        this.userHelper = userHelper;
    }

    public BookingUnit save(BookingUnit bookingUnit) {
        log.debug("Request to save BookingUnit : {}", bookingUnit);
        departmentAccessService.checkAccess(bookingUnit);
        checkBookingConflicts(bookingUnit);

        bookingUnit = bookingUnitRepository.save(bookingUnit);
        scheduleService.adjustSchedule(bookingUnit);
        return bookingUnit;
    }

    private void checkBookingConflicts(BookingUnit bookingUnit) {
        log.debug("Checking booking conflicts : {}", bookingUnit);

        Long bookingUnitId = bookingUnit.getId();
        ZonedDateTime startTime = bookingUnit.getStartTime().minusMinutes(TIME_SPACE_BETWEEN_BOOKING_UNITS);
        ZonedDateTime endTime = bookingUnit.getEndTime().plusMinutes(TIME_SPACE_BETWEEN_BOOKING_UNITS);
        DayOfWeek day = bookingUnit.getDay();
        WeekType week = bookingUnit.getWeek();
        SemesterHalf semesterHalf = bookingUnit.getSemesterHalf();
        Room room = bookingUnit.getRoom();
        ClassUnit classUnit = bookingUnit.getClassUnit();
        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        AcademicUnit academicUnit = classUnit.getAcademicUnit();
        AcademicUnitGroup academicUnitGroup = classUnit.getAcademicUnitGroup();
        UserExt user = classUnit.getUserExt();

        long conflictCount = bookingUnitRepository.getConflictCount(bookingUnitId, startTime, endTime, day, week,
            semesterHalf, classUnitGroup, room, user, academicUnit, academicUnitGroup);

        if(conflictCount > 0) {
            log.error("Booking conflict found! Breaking transaction...");
            throw new BookingConflictException();
        }
    }

    public List<BookingUnit> save(List<BookingUnit> bookingUnits) {
        log.debug("Request to save the list of BookingUnit : {}", bookingUnits);

        List<BookingUnit> results = new ArrayList<>();
        for(BookingUnit bookingUnit : bookingUnits) {
            BookingUnit savedBookingUnit = save(bookingUnit);
            results.add(savedBookingUnit);
        }

        return results;
    }

    @Transactional(readOnly = true)
    public Page<BookingUnit> findAll(Pageable pageable) {
        log.debug("Request to get all BookingUnits");
        return bookingUnitRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<BookingUnit> findById(Long id) {
        log.debug("Request to get BookingUnit : {}", id);
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return bookingUnitRepository.findByIdAndClassUnit_Subdepartment_Department_Id(id, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return bookingUnitRepository.findByIdAndClassUnit_Subdepartment_Id(id, subdepartmentId);
    }

    @Transactional(readOnly = true)
    public Optional<BookingUnit> findByClassUnitIdAndUser(Long classUnitId, UserExt userExt) {
        log.debug("Request to get BookingUnit by classUnitId: {} and user: {}", classUnitId, userExt);
        return bookingUnitRepository.findByClassUnit_IdAndClassUnit_UserExt(classUnitId, userExt);
    }

    @Transactional(readOnly = true)
    public List<BookingUnit> findByClassUnitGroupId(Long classUnitGroupId) {
        log.debug("Request to get BookingUnit by classUnitGroupId: {}", classUnitGroupId);
        return bookingUnitRepository.findByClassUnit_ClassUnitGroup_Id(classUnitGroupId);
    }

    public void delete(Long id) {
        log.debug("Request to delete BookingUnit : {}", id);
        findById(id).ifPresent(bookingUnitDTO -> {
            bookingUnitRepository.delete(id);
            scheduleService.removeClassUnitFromSchedule(bookingUnitDTO.getClassUnit().getId());
        });
    }

    public void deleteByClassUnitIdAndUser(Long classUnitId, UserExt user) {
        log.debug("Request to delete BookingUnit by classUnitId: {} and user: {}", classUnitId, user);
        bookingUnitRepository.deleteByClassUnit_IdAndClassUnit_UserExt(classUnitId, user);
        scheduleService.removeClassUnitFromSchedule(classUnitId);
    }
}
