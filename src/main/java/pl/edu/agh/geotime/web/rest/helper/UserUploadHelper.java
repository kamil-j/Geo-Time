package pl.edu.agh.geotime.web.rest.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.domain.UserGroup;
import pl.edu.agh.geotime.security.AuthoritiesConstants;
import pl.edu.agh.geotime.service.MailService;
import pl.edu.agh.geotime.service.SubdepartmentService;
import pl.edu.agh.geotime.service.UserGroupService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.web.rest.dto.ext.UserExtDTO;
import pl.edu.agh.geotime.web.rest.mapper.UserMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserUploadHelper {

    private final UserService userService;
    private final MailService mailService;
    private final SubdepartmentService subdepartmentService;
    private final UserGroupService userGroupService;
    private final UserMapper userMapper;

    @Autowired
    public UserUploadHelper(UserService userService, MailService mailService, SubdepartmentService subdepartmentService,
                            UserGroupService userGroupService, UserMapper userMapper) {
        this.userService = userService;
        this.mailService = mailService;
        this.subdepartmentService = subdepartmentService;
        this.userGroupService = userGroupService;
        this.userMapper = userMapper;
    }

    public void uploadUsersFromFile(MultipartFile file) throws IOException {
        List<UserExtDTO> usersToCreate = new ArrayList<>();

        InputStream is = file.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line;
        while ((line = br.readLine()) != null) {
            String[] userData = line.split(";");

            UserExtDTO userToCreate = createUserFromFileData(userData);
            usersToCreate.add(userToCreate);
        }

        usersToCreate.forEach(userExtDTO -> {
            UserExt newUserExt = userService.createUser(userMapper.toEntity(userExtDTO));
            mailService.sendCreationEmail(newUserExt.getUser());
        });
    }

    private UserExtDTO createUserFromFileData(String[] userData) {
        String login = userData[0];
        String lastName = userData[1];
        String firstName = userData[2];
        String email = userData[3];
        String subdepartmentShortName = userData[4];
        String userGroupName = userData[5];
        
        UserExtDTO userExtDTO = new UserExtDTO();
        userExtDTO.setLogin(login);
        userExtDTO.setFirstName(firstName);
        userExtDTO.setLastName(lastName);
        userExtDTO.setEmail(email);
        userExtDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));
        userExtDTO.setActivated(true);

        Subdepartment subdepartment = subdepartmentService.findByShortName(subdepartmentShortName).orElseThrow(() ->
            new BadRequestAlertException("Cannot find subdepartment with short name: " + subdepartmentShortName,
                "subdepartment", "notexists")
        );
        userExtDTO.setSubdepartmentId(subdepartment.getId());

        UserGroup userGroup = userGroupService.findByName(userGroupName).orElseThrow(() ->
            new BadRequestAlertException("Cannot find user group with name: " + userGroupName,
                "userGroup", "notexists")
        );
        userExtDTO.setUserGroupId(userGroup.getId());

        return userExtDTO;
    }
}
