package pl.edu.agh.geotime.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.service.errors.NotAllowedOperationException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
class DepartmentAccessService {

    private final StudyFieldService studyFieldService;
    private final RoomService roomService;
    private final ClassUnitService classUnitService;
    private final AcademicUnitService academicUnitService;
    private final ClassUnitGroupService classUnitGroupService;
    private final LocationService locationService;
    private final SubdepartmentService subdepartmentService;
    private final UserService userService;

    public DepartmentAccessService(@Lazy StudyFieldService studyFieldService, @Lazy RoomService roomService,
                                   @Lazy ClassUnitService classUnitService, @Lazy AcademicUnitService academicUnitService,
                                   @Lazy ClassUnitGroupService classUnitGroupService, @Lazy LocationService locationService,
                                   @Lazy SubdepartmentService subdepartmentService, @Lazy UserService userService) {
        this.studyFieldService = studyFieldService;
        this.roomService = roomService;
        this.classUnitService = classUnitService;
        this.academicUnitService = academicUnitService;
        this.classUnitGroupService = classUnitGroupService;
        this.locationService = locationService;
        this.subdepartmentService = subdepartmentService;
        this.userService = userService;
    }

    void checkAccess(AcademicUnit academicUnit) {
        checkAccessToStudyField(academicUnit.getStudyField());
    }

    void checkAccess(BookingUnit bookingUnit) {
        checkAccessToRoom(bookingUnit.getRoom());
        checkAccessToClassUnit(bookingUnit.getClassUnit());
    }

    void checkAccess(ClassUnit classUnit) {
        if(classUnit.getUserExt() != null) {
            checkAccessToUser(classUnit.getUserExt());
        }
        checkAccessToSubdepartment(classUnit.getSubdepartment());
        checkAccessToRooms(classUnit.getRooms());
        checkAccessToAcademicUnit(classUnit.getAcademicUnit());
        if(classUnit.getClassUnitGroup() != null) {
            checkAccessToClassUnitGroup(classUnit.getClassUnitGroup());
        }
    }

    void checkAccess(Room room) {
        checkAccessToLocation(room.getLocation());
    }

    void checkAccess(ScheduleUnit scheduleUnit) {
        checkAccessToRoom(scheduleUnit.getRoom());
        checkAccessToClassUnit(scheduleUnit.getClassUnit());
    }

    void checkAccess(SchedulingTimeFrame schedulingTimeFrame) {
        checkAccessToSubdepartment(schedulingTimeFrame.getSubdepartment());
    }

    void checkAccess(UserExt userExt) {
        checkAccessToSubdepartment(userExt.getSubdepartment());
    }

    private void checkAccessToStudyField(StudyField studyField) {
        if(!studyFieldService.findById(studyField.getId()).isPresent()) {
            throw new NotAllowedOperationException("studyField", studyField.getId());
        }
    }

    private void checkAccessToClassUnit(ClassUnit classUnit) {
        if (!classUnitService.findById(classUnit.getId()).isPresent()) {
            throw new NotAllowedOperationException("classUnit", classUnit.getId());
        }
    }

    private void checkAccessToUser(UserExt userExt) {
        if (!userService.findById(userExt.getId()).isPresent()) {
            throw new NotAllowedOperationException("userExt", userExt.getId());
        }
    }

    private void checkAccessToRoom(Room room) {
        if (!roomService.findById(room.getId()).isPresent()) {
            throw new NotAllowedOperationException("room", room.getId());
        }
    }

    private void checkAccessToClassUnitGroup(ClassUnitGroup classUnitGroup) {
        if(!classUnitGroupService.findById(classUnitGroup.getId()).isPresent()) {
            throw new NotAllowedOperationException("classUnitGroup", classUnitGroup.getId());
        }
    }

    private void checkAccessToLocation(Location location) {
        if(!locationService.findById(location.getId()).isPresent()) {
            throw new NotAllowedOperationException("location", location.getId());
        }
    }

    private void checkAccessToAcademicUnit(AcademicUnit academicUnit) {
        if(!academicUnitService.findById(academicUnit.getId()).isPresent()) {
            throw new NotAllowedOperationException("academicUnit", academicUnit.getId());
        }
    }

    private void checkAccessToSubdepartment(Subdepartment subdepartment) {
        if(!subdepartmentService.findById(subdepartment.getId()).isPresent()) {
            throw new NotAllowedOperationException("subdepartment", subdepartment.getId());
        }
    }

    private void checkAccessToRooms(Set<Room> rooms) {
        Set<Long> roomsId = rooms.stream()
            .map(Room::getId)
            .collect(Collectors.toSet());
        Set<Long> roomsIdFromDB = roomService.findByIdIn(roomsId).stream()
            .map(Room::getId)
            .collect(Collectors.toSet());
        roomsId.removeAll(roomsIdFromDB);
        if(!roomsId.isEmpty()) {
            throw new NotAllowedOperationException("room", roomsId);
        }
    }
}
