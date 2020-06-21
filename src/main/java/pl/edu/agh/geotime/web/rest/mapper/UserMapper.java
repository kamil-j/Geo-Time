package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Authority;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.web.rest.dto.ext.UserExtDTO;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SubdepartmentMapper.class, UserGroupMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends EntityMapper<UserExtDTO, UserExt> {

    @Mapping(source = "subdepartmentId", target = "subdepartment")
    @Mapping(source = "userGroupId", target = "userGroup")
    @Mapping(source = "id", target = "user.id")
    @Mapping(source = "login", target = "user.login")
    @Mapping(source = "firstName", target = "user.firstName")
    @Mapping(source = "lastName", target = "user.lastName")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "activated", target = "user.activated")
    @Mapping(source = "langKey", target = "user.langKey")
    @Mapping(source = "authorities", target = "user.authorities")
    @Mapping(target = "user.createdBy", ignore = true)
    @Mapping(target = "user.createdDate", ignore = true)
    @Mapping(target = "user.lastModifiedBy", ignore = true)
    @Mapping(target = "user.lastModifiedDate", ignore = true)
    @Mapping(target = "classUnits", ignore = true)
    UserExt toEntity(UserExtDTO userDTO);

    @Mapping(source = "subdepartment.id", target = "subdepartmentId")
    @Mapping(source = "subdepartment.shortName", target = "subdepartmentShortName")
    @Mapping(source = "userGroup.id", target = "userGroupId")
    @Mapping(source = "userGroup.name", target = "userGroupName")
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.login", target = "login")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.activated", target = "activated")
    @Mapping(source = "user.langKey", target = "langKey")
    @Mapping(source = "user.createdBy", target = "createdBy")
    @Mapping(source = "user.createdDate", target = "createdDate")
    @Mapping(source = "user.lastModifiedBy", target = "lastModifiedBy")
    @Mapping(source = "user.lastModifiedDate", target = "lastModifiedDate")
    @Mapping(source = "user.authorities", target = "authorities")
    UserExtDTO toDto(UserExt user);

    @SuppressWarnings("unused") //Used by mapper
    default Set<Authority> authoritiesFromStrings(Set<String> strings) {
        if(strings == null) {
            return Collections.emptySet();
        }
        return strings.stream().map(string -> {
            Authority auth = new Authority();
            auth.setName(string);
            return auth;
        }).collect(Collectors.toSet());
    }

    @SuppressWarnings("unused") //Used by mapper
    default Set<String> authoritiesToStrings(Set<Authority> authorities) {
        if(authorities == null) {
            return Collections.emptySet();
        }
        return authorities.stream()
            .map(Authority::getName)
            .collect(Collectors.toSet());
    }

    default UserExt fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserExt userExt = new UserExt();
        userExt.setId(id);
        return userExt;
    }
}
