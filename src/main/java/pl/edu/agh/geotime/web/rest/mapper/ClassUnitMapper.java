package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitDTO;

@Mapper(componentModel = "spring", uses = {ClassTypeMapper.class, UserMapper.class, RoomMapper.class,
    AcademicUnitMapper.class, SemesterMapper.class, ClassUnitGroupMapper.class, SubdepartmentMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassUnitMapper extends EntityMapper<ClassUnitDTO, ClassUnit> {

    @Mapping(source = "classType.id", target = "classTypeId")
    @Mapping(source = "classType.name", target = "classTypeName")
    @Mapping(source = "userExt.id", target = "userId")
    @Mapping(source = "userExt.user.login", target = "userLogin")
    @Mapping(source = "academicUnit.id", target = "academicUnitId")
    @Mapping(source = "academicUnit.name", target = "academicUnitName")
    @Mapping(source = "semester.id", target = "semesterId")
    @Mapping(source = "semester.name", target = "semesterName")
    @Mapping(source = "classUnitGroup.id", target = "classUnitGroupId")
    @Mapping(source = "classUnitGroup.name", target = "classUnitGroupName")
    @Mapping(source = "subdepartment.id", target = "subdepartmentId")
    @Mapping(source = "subdepartment.shortName", target = "subdepartmentShortName")
    ClassUnitDTO toDto(ClassUnit classUnit);

    @Mapping(source = "classTypeId", target = "classType")
    @Mapping(source = "userId", target = "userExt")
    @Mapping(source = "academicUnitId", target = "academicUnit")
    @Mapping(source = "semesterId", target = "semester")
    @Mapping(source = "classUnitGroupId", target = "classUnitGroup")
    @Mapping(source = "subdepartmentId", target = "subdepartment")
    ClassUnit toEntity(ClassUnitDTO classUnitDTO);

    default ClassUnit fromId(Long id) {
        if (id == null) {
            return null;
        }
        ClassUnit classUnit = new ClassUnit();
        classUnit.setId(id);
        return classUnit;
    }
}
