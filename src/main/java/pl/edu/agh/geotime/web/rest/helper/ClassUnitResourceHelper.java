package pl.edu.agh.geotime.web.rest.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.service.ClassUnitGroupService;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.vm.ClassUnitCreateVM;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassUnitResourceHelper {

    private final ClassUnitService classUnitService;
    private final ClassUnitGroupService classUnitGroupService;
    private final UserService userService;

    @Autowired
    public ClassUnitResourceHelper(ClassUnitService classUnitService, ClassUnitGroupService classUnitGroupService,
                                   UserService userService) {
        this.classUnitService = classUnitService;
        this.classUnitGroupService = classUnitGroupService;
        this.userService = userService;
    }

    public ClassUnit update(ClassUnit classUnit) {
        if(classUnit.getUserExt() != null) {
            Long userId = classUnit.getUserExt().getId();
            UserExt user = userService.findById(userId)
                .orElseThrow(() -> new BadRequestAlertException("User does not have access to user with id: "
                    + userId, "user", "notallowed"));
            classUnit.setSubdepartment(user.getSubdepartment());
        }
        return classUnitService.save(classUnit);
    }

    public ClassUnit create(ClassUnitCreateVM classUnitCreateVM) {
        ClassUnit classUnit = mapToEntity(classUnitCreateVM);
        return classUnitService.save(classUnit);
    }

    private ClassUnit mapToEntity(ClassUnitCreateVM classUnitCreateVM) {
        ClassUnit classUnit = new ClassUnit();
        classUnit.setTitle(classUnitCreateVM.getTitle());
        classUnit.setDescription(classUnitCreateVM.getDescription());
        classUnit.setDuration(classUnitCreateVM.getDuration());
        classUnit.setHoursQuantity(classUnitCreateVM.getHoursQuantity());
        classUnit.setFrequency(classUnitCreateVM.getFrequency());
        classUnit.setAcademicUnitGroup(classUnitCreateVM.getAcademicUnitGroup());

        ClassType classType = new ClassType();
        classType.setId(classUnitCreateVM.getClassTypeId());
        classUnit.setClassType(classType);

        Set<Room> rooms = classUnitCreateVM.getRoomIds().stream()
            .map(roomId -> {
                Room room = new Room();
                room.setId(roomId);
                return room;
            }).collect(Collectors.toSet());
        classUnit.setRooms(rooms);

        AcademicUnit academicUnit = new AcademicUnit();
        academicUnit.setId(classUnitCreateVM.getAcademicUnitId());
        classUnit.setAcademicUnit(academicUnit);

        Semester semester = new Semester();
        semester.setId(classUnitCreateVM.getSemesterId());
        classUnit.setSemester(semester);

        ClassUnitGroup classUnitGroup = classUnitCreateVM.getClassUnitGroup();
        if(classUnitGroup != null && classUnitGroup.getId() == null) {
            if(classUnitGroup.getName() != null) {
                classUnitGroup = classUnitGroupService.save(classUnitGroup);
            } else {
                classUnitGroup = null;
            }
        }
        classUnit.setClassUnitGroup(classUnitGroup);

        Subdepartment subdepartment = new Subdepartment();
        if(classUnitCreateVM.getSubdepartmentId() != null){
            subdepartment.setId(classUnitCreateVM.getSubdepartmentId());
        } else if(classUnitCreateVM.getUserId() != null) {
            UserExt user = userService.findById(classUnitCreateVM.getUserId())
                .orElseThrow(() -> new BadRequestAlertException("User does not have access to user with id: "
                    + classUnitCreateVM.getUserId(), "user", "notallowed"));
            subdepartment.setId(user.getSubdepartment().getId());
            classUnit.setUserExt(user);
        } else {
            throw new BadRequestAlertException("SubdepartmentId cannot be empty if UserId is empty!",
                "subdepartment", "notallowed");
        }
        classUnit.setSubdepartment(subdepartment);

        return classUnit;
    }
}
