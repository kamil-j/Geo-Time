package pl.edu.agh.geotime.web.rest.helper;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.BookingUnit;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;
import pl.edu.agh.geotime.domain.enumeration.WeekType;
import pl.edu.agh.geotime.service.BookingUnitInfoService;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.web.rest.vm.AcademicUnitBookingConflictVM;
import pl.edu.agh.geotime.web.rest.vm.RoomBookingConflictVM;
import pl.edu.agh.geotime.web.rest.vm.UserBookingConflictVM;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingConflictsHelper {

    private final BookingUnitInfoService bookingUnitInfoService;
    private final ClassUnitService classUnitService;

    @Autowired
    public BookingConflictsHelper(BookingUnitInfoService bookingUnitInfoService, ClassUnitService classUnitService) {
        this.bookingUnitInfoService = bookingUnitInfoService;
        this.classUnitService = classUnitService;
    }

    public List<RoomBookingConflictVM> getRoomBookingConflicts(Long roomId, UserExt user, SemesterHalf semesterHalf,
                                                               WeekType weekType, ClassFrequency frequency) {
        Set<Long> roomIds;
        if(roomId == null) {
            roomIds = getAllRoomIdsFromUserBookings(user, semesterHalf, weekType);
            if(roomIds.isEmpty()) {
                return Collections.emptyList();
            }
        } else {
            roomIds = Collections.singleton(roomId);
        }

        return findRoomsBookingConflicts(roomIds, user, semesterHalf, weekType, frequency);
    }

    private Set<Long> getAllRoomIdsFromUserBookings(UserExt user, SemesterHalf semesterHalf, WeekType weekType) {
        List<BookingUnit> bookingUnits = bookingUnitInfoService.getUserBookings(user, semesterHalf, weekType);
        if(bookingUnits.isEmpty()) {
            return Collections.emptySet();
        }
        return getRoomIdsFromBookings(bookingUnits);
    }

    private Set<Long> getRoomIdsFromBookings(List<BookingUnit> bookingUnits) {
        return bookingUnits.stream()
            .map(bookingUnit -> bookingUnit.getRoom().getId())
            .collect(Collectors.toSet());
    }

    private List<RoomBookingConflictVM> findRoomsBookingConflicts(Set<Long> roomIds, UserExt user, SemesterHalf semesterHalf,
                                                                  WeekType weekType, ClassFrequency frequency) {
        return bookingUnitInfoService.getRoomsBookings(roomIds)
            .stream()
            .filter(bookingUnit -> filterBookingUnits(bookingUnit, user, semesterHalf, weekType, frequency))
            .map(RoomBookingConflictVM::new)
            .collect(Collectors.toList());
    }

    private boolean filterBookingUnits(BookingUnit bookingUnit, UserExt user, SemesterHalf semesterHalf, WeekType weekType,
                                       ClassFrequency frequency) {
        if(isFromCurrentUserScreen(bookingUnit, user, semesterHalf, weekType)) {
            return false;
        }
        if(frequency.equals(ClassFrequency.SINGLE)) {
            return semesterHalf.equals(bookingUnit.getSemesterHalf()) && weekType.equals(bookingUnit.getWeek());
        }

        boolean onlySemesterHalf = bookingUnit.getClassUnit().isOnlySemesterHalf();
        boolean hasSemesterHalf1 = bookingUnit.getSemesterHalf().equals(SemesterHalf.HALF1);
        boolean hasWeekA = bookingUnit.getWeek().equals(WeekType.A);
        boolean isEveryTwoWeeks = bookingUnit.getClassUnit().getFrequency().equals(ClassFrequency.EVERY_TWO_WEEKS);

        if(semesterHalf.equals(SemesterHalf.HALF2)) {
            if(weekType.equals(WeekType.A)) {
                return !(hasSemesterHalf1 && onlySemesterHalf) && (hasWeekA || !frequency.equals(ClassFrequency.EVERY_TWO_WEEKS));
            }
            return !(hasWeekA && isEveryTwoWeeks) && !(hasSemesterHalf1 && onlySemesterHalf);
        }

        if(weekType.equals(WeekType.B)) {
            return !(hasWeekA && isEveryTwoWeeks);
        }
        return hasWeekA || !frequency.equals(ClassFrequency.EVERY_TWO_WEEKS);
    }

    private boolean isFromCurrentUserScreen(BookingUnit bookingUnit, UserExt user, SemesterHalf semesterHalf, WeekType weekType) {
        boolean hasSameUser = bookingUnit.getClassUnit().getUserExt().getId().equals(user.getId());
        boolean hasSameWeek = bookingUnit.getWeek().equals(weekType);
        boolean hasSameSemesterHalf = bookingUnit.getSemesterHalf().equals(semesterHalf);
        return hasSameUser && hasSameWeek && hasSameSemesterHalf;
    }

    public List<AcademicUnitBookingConflictVM> getAcademicUnitBookingConflicts(Long classUnitId, UserExt user,
                                                                               SemesterHalf semesterHalf, WeekType weekType,
                                                                               ClassFrequency frequency) {

        List<AcademicUnitBookingConflictVM> academicUnitBookingConflicts;
        if(classUnitId == null) {
            academicUnitBookingConflicts = getAllUserAcademicUnitBookingConflicts(user, semesterHalf, weekType, frequency);
        } else {
            academicUnitBookingConflicts = getUserAcademicUnitBookingConflicts(classUnitId, user, semesterHalf, weekType, frequency);
        }
        return academicUnitBookingConflicts;
    }

    private List<AcademicUnitBookingConflictVM> getAllUserAcademicUnitBookingConflicts(UserExt user, SemesterHalf semesterHalf,
                                                                                       WeekType weekType, ClassFrequency frequency) {
        List<BookingUnit> bookingUnits = bookingUnitInfoService.getUserBookings(user, semesterHalf, weekType);
        if(bookingUnits.isEmpty()) {
            return Collections.emptyList();
        }

        Set<AcademicUnitGroupDTO> academicUnitGroups = getAllAcademicUnitGroupsFromUserBookings(bookingUnits);
        return findAcademicUnitsBookingConflicts(academicUnitGroups, user, semesterHalf, weekType, frequency);
    }

    private Set<AcademicUnitGroupDTO> getAllAcademicUnitGroupsFromUserBookings(List<BookingUnit> bookingUnits) {
        Set<AcademicUnitGroupDTO> academicUnitGroups = getAcademicUnitGroupsFromBookings(bookingUnits);

        Set<Long> classUnitGroupIds = getClassUnitGroupIdsFromBookings(bookingUnits);
        if(!classUnitGroupIds.isEmpty()) {
            Set<AcademicUnitGroupDTO> academicUnitGroupsFromClassUnitGroups = getAllAcademicUnitGroupsFromClassUnitGroups(
                classUnitGroupIds);
            academicUnitGroups.addAll(academicUnitGroupsFromClassUnitGroups);
        }

        return academicUnitGroups;
    }

    private Set<AcademicUnitGroupDTO> getAcademicUnitGroupsFromBookings(List<BookingUnit> bookingUnits) {
        return bookingUnits.stream()
            .map(bookingUnit -> new AcademicUnitGroupDTO(bookingUnit.getClassUnit()))
            .collect(Collectors.toSet());
    }

    private Set<Long> getClassUnitGroupIdsFromBookings(List<BookingUnit> bookingUnits) {
        return bookingUnits.stream()
            .map(bookingUnit -> {
                ClassUnitGroup classUnitGroup = bookingUnit.getClassUnit().getClassUnitGroup();
                return classUnitGroup != null ? classUnitGroup.getId() : null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private Set<AcademicUnitGroupDTO> getAllAcademicUnitGroupsFromClassUnitGroups(Set<Long> classUnitGroupIds) {
        return classUnitService.findBookedByClassUnitGroupIdIn(classUnitGroupIds)
            .stream()
            .map(AcademicUnitGroupDTO::new)
            .collect(Collectors.toSet());
    }

    private List<AcademicUnitBookingConflictVM> findAcademicUnitsBookingConflicts(Set<AcademicUnitGroupDTO> academicUnitGroups,
                                                                                  UserExt user, SemesterHalf semesterHalf,
                                                                                  WeekType weekType, ClassFrequency frequency) {
        return bookingUnitInfoService.getAcademicUnitBookings(academicUnitGroups)
            .stream()
            .filter(bookingUnit -> filterBookingUnits(bookingUnit, user, semesterHalf, weekType, frequency))
            .map(AcademicUnitBookingConflictVM::new)
            .collect(Collectors.toList());
    }

    private List<AcademicUnitBookingConflictVM> getUserAcademicUnitBookingConflicts(Long classUnitId, UserExt user,
                                                                                    SemesterHalf semesterHalf,
                                                                                    WeekType weekType, ClassFrequency frequency) {
        Optional<ClassUnit> classUnit = classUnitService.findByIdAndUser(classUnitId, user);
        if(!classUnit.isPresent()) {
            return Collections.emptyList();
        }

        Set<AcademicUnitGroupDTO> academicUnitGroups = getAllAcademicUnitGroupsFromClassUnit(classUnit.get());
        return findAcademicUnitsBookingConflicts(academicUnitGroups, user, semesterHalf, weekType, frequency);
    }

    private Set<AcademicUnitGroupDTO> getAllAcademicUnitGroupsFromClassUnit(ClassUnit classUnit) {
        Set<AcademicUnitGroupDTO> academicUnitGroups = Sets.newHashSet(new AcademicUnitGroupDTO(classUnit));

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        if(classUnitGroup != null) {
            Set<AcademicUnitGroupDTO> academicUnitGroupsFromClassUnitGroups = getAllAcademicUnitGroupsFromClassUnitGroup(
                classUnitGroup.getId());
            academicUnitGroups.addAll(academicUnitGroupsFromClassUnitGroups);
        }

        return academicUnitGroups;
    }

    private Set<AcademicUnitGroupDTO> getAllAcademicUnitGroupsFromClassUnitGroup(Long classUnitGroupId) {
        return classUnitService.findNotBookedByClassUnitGroupId(classUnitGroupId)
            .stream()
            .map(AcademicUnitGroupDTO::new)
            .collect(Collectors.toSet());
    }

    public List<UserBookingConflictVM> getUserBookingConflicts(Set<Long> userIds, UserExt user, SemesterHalf semesterHalf,
                                                               WeekType weekType, ClassFrequency frequency) {
        if(userIds == null || userIds.isEmpty()) {
            userIds = getAllUserIdsFromUserBookings(user, semesterHalf, weekType);
            if(userIds.isEmpty()) {
                return Collections.emptyList();
            }
        }

        return findUsersBookingConflicts(userIds, user, semesterHalf, weekType, frequency);
    }

    private Set<Long> getAllUserIdsFromUserBookings(UserExt user, SemesterHalf semesterHalf, WeekType weekType) {
        List<BookingUnit> bookingUnits = bookingUnitInfoService.getUserBookings(user, semesterHalf, weekType);
        if(bookingUnits.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> classUnitGroupIds = getClassUnitGroupIdsFromBookings(bookingUnits);
        if(classUnitGroupIds.isEmpty()) {
            return Collections.emptySet();
        }

        return getAllUserIdsFromClassUnitGroups(classUnitGroupIds, user);
    }

    private Set<Long> getAllUserIdsFromClassUnitGroups(Set<Long> classUnitGroupIds, UserExt user) {
        return classUnitService.findBookedByUserIsNotAndClassUnitGroupIdIn(user, classUnitGroupIds)
            .stream()
            .map(classUnit -> classUnit.getUserExt().getId())
            .collect(Collectors.toSet());
    }

    private List<UserBookingConflictVM> findUsersBookingConflicts(Set<Long> userIds, UserExt user, SemesterHalf semesterHalf,
                                                                  WeekType weekType, ClassFrequency frequency) {
        return bookingUnitInfoService.getUsersBookings(userIds)
            .stream()
            .filter(bookingUnit -> filterBookingUnits(bookingUnit, user, semesterHalf, weekType, frequency))
            .map(UserBookingConflictVM::new)
            .collect(Collectors.toList());
    }
}
