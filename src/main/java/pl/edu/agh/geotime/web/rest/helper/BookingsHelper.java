package pl.edu.agh.geotime.web.rest.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.BookingUnitInfoService;
import pl.edu.agh.geotime.service.BookingUnitService;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.util.ClassUnitUtil;
import pl.edu.agh.geotime.service.util.DateUtil;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.mapper.TimetableBookingMapper;
import pl.edu.agh.geotime.web.rest.validator.TimetableValidator;
import pl.edu.agh.geotime.web.rest.vm.CreateBookingVM;
import pl.edu.agh.geotime.web.rest.vm.LockBookingVM;
import pl.edu.agh.geotime.web.rest.vm.TimetableBookingVM;
import pl.edu.agh.geotime.web.rest.vm.UpdateBookingVM;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingsHelper {

    private final BookingUnitInfoService bookingUnitInfoService;
    private final BookingUnitService bookingUnitService;
    private final TimetableBookingMapper timetableBookingMapper;
    private final ClassUnitService classUnitService;
    private final TimetableValidator timetableValidator;

    @Autowired
    public BookingsHelper(BookingUnitInfoService bookingUnitInfoService, BookingUnitService bookingUnitService,
                          TimetableBookingMapper timetableBookingMapper, ClassUnitService classUnitService,
                          TimetableValidator timetableValidator) {
        this.bookingUnitInfoService = bookingUnitInfoService;
        this.bookingUnitService = bookingUnitService;
        this.timetableBookingMapper = timetableBookingMapper;
        this.classUnitService = classUnitService;
        this.timetableValidator = timetableValidator;
    }

    public List<TimetableBookingVM> getUserBookings(UserExt userExt, SemesterHalf semesterHalf, WeekType weekType) {
        List<BookingUnit> bookings = bookingUnitInfoService.getUserBookings(userExt, semesterHalf, weekType);
        return bookings.stream()
            .map(timetableBookingMapper::toTimetableBooking)
            .collect(Collectors.toList());
    }

    public TimetableBookingVM createBooking(CreateBookingVM createBookingVM, UserExt user, boolean hasFullAccess) {
        ClassUnit classUnit = getNotBookedClassUnitByUserAndId(user, createBookingVM.getClassUnitId());

        timetableValidator.validateCreateBookingRequest(createBookingVM, classUnit, user, hasFullAccess);

        Room room = getRoomFromClassUnit(classUnit, createBookingVM.getRoomId());
        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();

        BookingUnit savedBookingUnit;
        if(classUnitGroup != null) {
            List<BookingUnit> bookings = createBookingsForClassUnitGroup(createBookingVM, classUnitGroup.getId(), room);
            savedBookingUnit = bookingUnitService.save(bookings).stream()
                .filter(bookingUnitDTO -> bookingUnitDTO.getClassUnit().getId().equals(classUnit.getId()))
                .findAny()
                .orElse(null);
        } else {
            BookingUnit bookingUnit = timetableBookingMapper.toBookingUnit(createBookingVM, classUnit, room);
            savedBookingUnit = bookingUnitService.save(bookingUnit);
        }

        return timetableBookingMapper.toTimetableBooking(savedBookingUnit);
    }

    private ClassUnit getNotBookedClassUnitByUserAndId(UserExt user, Long classUnitId) {
        return classUnitService.findNotBookedByIdAndUser(classUnitId, user)
            .orElseThrow(
                () -> new BadRequestAlertException("Entity ClassUnit not found!", "classUnit", "notexists")
            );
    }

    private Room getRoomFromClassUnit(ClassUnit classUnit, Long roomId) {
        return ClassUnitUtil.getRoomWithId(classUnit, roomId).orElseThrow(
            () -> new BadRequestAlertException("ClassUnit with id: " + classUnit.getId()
                + " does not contain Room with id: " + roomId + "!", "classUnit", "notexists")
        );
    }

    private List<BookingUnit> createBookingsForClassUnitGroup(CreateBookingVM createBookingVM, Long classUnitGroupId,
                                                                 Room room) {
        Set<ClassUnit> classUnits = classUnitService.findNotBookedByClassUnitGroupId(classUnitGroupId);

        return classUnits.stream()
            .map(classUnit -> timetableBookingMapper.toBookingUnit(createBookingVM, classUnit, room))
            .collect(Collectors.toList());
    }

    public TimetableBookingVM updateBooking(UpdateBookingVM updateBookingVM, UserExt user, boolean hasFullAccess) {
        BookingUnit existingBookingUnit = getBookingUnitByClassUnitIdByUser(updateBookingVM.getClassUnitId(), user);
        ClassUnit classUnit = existingBookingUnit.getClassUnit();

        timetableValidator.validateUpdateBookingRequest(updateBookingVM, existingBookingUnit, user, hasFullAccess);

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();

        BookingUnit updatedBookingUnit;
        if(classUnitGroup != null) {
            updatedBookingUnit = updateClassUnitGroupBookings(updateBookingVM, classUnitGroup.getId())
                .stream()
                .filter(bookingUnitDTO -> bookingUnitDTO.getClassUnit().getId().equals(classUnit.getId()))
                .findAny()
                .orElse(null);
        } else {
            updatedBookingUnit = updateBookingUnit(existingBookingUnit, updateBookingVM);
        }

        return timetableBookingMapper.toTimetableBooking(updatedBookingUnit);
    }

    private BookingUnit getBookingUnitByClassUnitIdByUser(Long classUnitId, UserExt user) {
        return bookingUnitService.findByClassUnitIdAndUser(classUnitId, user)
            .orElseThrow(
                () -> new BadRequestAlertException("Entity BookingUnit not found!", "bookingUnit", "notexists")
            );
    }

    private List<BookingUnit> updateClassUnitGroupBookings(UpdateBookingVM updateBookingVM, Long classUnitGroupId) {
        List<BookingUnit> bookingUnits = bookingUnitService.findByClassUnitGroupId(classUnitGroupId);

        return bookingUnits.stream()
            .map(bookingUnit -> updateBookingUnit(bookingUnit, updateBookingVM))
            .collect(Collectors.toList());
    }

    private BookingUnit updateBookingUnit(BookingUnit bookingUnit, UpdateBookingVM updateBookingVM) {
        bookingUnit.setStartTime(DateUtil.convertToZonedDateTime(updateBookingVM.getStartTime()));
        bookingUnit.setEndTime(DateUtil.convertToZonedDateTime(updateBookingVM.getEndTime()));
        bookingUnit.setDay(updateBookingVM.getDay());
        bookingUnit.setWeek(updateBookingVM.getWeek());
        bookingUnit.setSemesterHalf(updateBookingVM.getSemesterHalf());
        return bookingUnitService.save(bookingUnit);
    }

    public void deleteBooking(Long classUnitId, UserExt user, boolean hasFullAccess) {
        BookingUnit existingBookingUnit = getBookingUnitByClassUnitIdByUser(classUnitId, user);
        ClassUnit classUnit = existingBookingUnit.getClassUnit();

        timetableValidator.validateDeleteBookingRequest(existingBookingUnit, user, hasFullAccess);

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        if(classUnitGroup != null) {
            deleteClassUnitGroupBookings(classUnitGroup.getId());
        } else {
            bookingUnitService.deleteByClassUnitIdAndUser(classUnitId, user);
        }
    }

    private void deleteClassUnitGroupBookings(Long classUnitGroupId) {
        bookingUnitService.findByClassUnitGroupId(classUnitGroupId)
            .forEach(bookingUnitDTO -> bookingUnitService.delete(bookingUnitDTO.getId()));
    }

    public TimetableBookingVM lockBooking(LockBookingVM lockBookingVM, UserExt user) {
        BookingUnit existingBookingUnit = getBookingUnitByClassUnitIdByUser(lockBookingVM.getClassUnitId(), user);
        ClassUnit classUnit = existingBookingUnit.getClassUnit();
        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();

        BookingUnit lockedBookingUnit;
        if(classUnitGroup != null) {
            lockedBookingUnit = lockClassUnitGroupBookings(lockBookingVM, classUnitGroup.getId())
                .stream()
                .filter(bookingUnit -> bookingUnit.getClassUnit().getId().equals(classUnit.getId()))
                .findAny()
                .orElse(null);
        } else {
            lockedBookingUnit = lockBookingUnit(existingBookingUnit, lockBookingVM);
        }

        return timetableBookingMapper.toTimetableBooking(lockedBookingUnit);
    }

    private List<BookingUnit> lockClassUnitGroupBookings(LockBookingVM lockBookingVM, Long classUnitGroupId) {
        List<BookingUnit> bookingUnits = bookingUnitService.findByClassUnitGroupId(classUnitGroupId);

        return bookingUnits.stream()
            .map(bookingUnit -> lockBookingUnit(bookingUnit, lockBookingVM))
            .collect(Collectors.toList());
    }

    private BookingUnit lockBookingUnit(BookingUnit bookingUnit, LockBookingVM lockBookingVM) {
        boolean lock = lockBookingVM.getLock() != null ? lockBookingVM.getLock() : true;
        bookingUnit.setLocked(lock);

        return bookingUnitService.save(bookingUnit);
    }
}
