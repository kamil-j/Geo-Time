package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.UserGroup;
import pl.edu.agh.geotime.web.rest.dto.UserGroupDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserGroupMapper extends EntityMapper<UserGroupDTO, UserGroup> {

    @Mapping(target = "usersExt", ignore = true)
    @Mapping(target = "schedulingTimeFrames", ignore = true)
    UserGroup toEntity(UserGroupDTO userGroupDTO);

    default UserGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setId(id);
        return userGroup;
    }
}
