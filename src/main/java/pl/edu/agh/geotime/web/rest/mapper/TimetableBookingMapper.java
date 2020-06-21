package pl.edu.agh.geotime.web.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.Room;
import pl.edu.agh.geotime.repository.ClassUnitGroupRepository;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.service.util.DateUtil;
import pl.edu.agh.geotime.web.rest.vm.CreateBookingVM;
import pl.edu.agh.geotime.web.rest.vm.TimetableBookingVM;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TimetableBookingMapper {

    private final ClassUnitGroupRepository classUnitGroupRepository;

    @Autowired
    public TimetableBookingMapper(ClassUnitGroupRepository classUnitGroupRepository) {
        this.classUnitGroupRepository = classUnitGroupRepository;
    }

    public BookingUnit toBookingUnit(CreateBookingVM createBookingVM, ClassUnit classUnit, Room room) {
        if (createBookingVM == null) {
            return null;
        }

        BookingUnit bookingUnit = new BookingUnit();
        bookingUnit.setStartTime(DateUtil.convertToZonedDateTime(createBookingVM.getStartTime()));
        bookingUnit.setEndTime(DateUtil.convertToZonedDateTime(createBookingVM.getEndTime()));
        bookingUnit.setDay(createBookingVM.getDay());
        bookingUnit.setWeek(createBookingVM.getWeek());
        bookingUnit.setSemesterHalf(createBookingVM.getSemesterHalf());
        bookingUnit.setClassUnit(classUnit);
        bookingUnit.setRoom(room);
        bookingUnit.setLocked(false);

        return bookingUnit;
    }

    public TimetableBookingVM toTimetableBooking(BookingUnit bookingUnit) {
        if (bookingUnit == null) {
            return null;
        }

        TimetableBookingVM timetableBooking = new TimetableBookingVM(bookingUnit);
        ClassUnit classUnit = bookingUnit.getClassUnit();

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        if(classUnitGroup != null) {
            Set<ClassUnit> classUnits = classUnitGroupRepository.findByIdWithClassUnits(classUnitGroup.getId())
                .getClassUnits();

            Long academicUnitIdFromScheduleUnit = classUnit.getAcademicUnit().getId();
            timetableBooking.setRelatedAcademicUnitGroups(getRelatedAcademicUnitGroups(classUnits, academicUnitIdFromScheduleUnit));

            Long userIdFromScheduleUnit = classUnit.getUserExt().getId();
            timetableBooking.setRelatedUserIds(getRelatedUserIds(classUnits, userIdFromScheduleUnit));
        }

        return timetableBooking;
    }

    private Set<AcademicUnitGroupDTO> getRelatedAcademicUnitGroups(Set<ClassUnit> classUnits, Long academicUnitIdFromScheduleUnit) {
        return classUnits.stream()
            .map(AcademicUnitGroupDTO::new)
            .filter(relatedAcademicUnit -> !relatedAcademicUnit.getAcademicUnitId().equals(academicUnitIdFromScheduleUnit))
            .collect(Collectors.toSet());
    }

    private Set<Long> getRelatedUserIds(Set<ClassUnit> classUnits, Long userIdFromScheduleUnit) {
        return classUnits.stream()
            .map(classUnit -> classUnit.getUserExt().getId())
            .filter(userId -> !userId.equals(userIdFromScheduleUnit))
            .collect(Collectors.toSet());
    }
}
