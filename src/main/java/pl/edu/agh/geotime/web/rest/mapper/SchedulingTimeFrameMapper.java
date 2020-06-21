package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.web.rest.dto.SchedulingTimeFrameDTO;

@Mapper(componentModel = "spring", uses = {UserGroupMapper.class, SubdepartmentMapper.class, SemesterMapper.class})
public interface SchedulingTimeFrameMapper extends EntityMapper<SchedulingTimeFrameDTO, SchedulingTimeFrame> {

    @Mapping(source = "userGroup.id", target = "userGroupId")
    @Mapping(source = "userGroup.name", target = "userGroupName")
    @Mapping(source = "subdepartment.id", target = "subdepartmentId")
    @Mapping(source = "subdepartment.shortName", target = "subdepartmentShortName")
    @Mapping(source = "semester.id", target = "semesterId")
    @Mapping(source = "semester.name", target = "semesterName")
    SchedulingTimeFrameDTO toDto(SchedulingTimeFrame schedulingTimeFrame);

    @Mapping(source = "userGroupId", target = "userGroup")
    @Mapping(source = "subdepartmentId", target = "subdepartment")
    @Mapping(source = "semesterId", target = "semester")
    SchedulingTimeFrame toEntity(SchedulingTimeFrameDTO schedulingTimeFrameDTO);

    default SchedulingTimeFrame fromId(Long id) {
        if (id == null) {
            return null;
        }
        SchedulingTimeFrame schedulingTimeFrame = new SchedulingTimeFrame();
        schedulingTimeFrame.setId(id);
        return schedulingTimeFrame;
    }
}
