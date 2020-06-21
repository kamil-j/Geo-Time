package pl.edu.agh.geotime.service.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.service.errors.NotAllowedOperationException;

@Component
public class UserHelper {

    private final UserService userService;

    @Autowired
    public UserHelper(UserService userService) {
        this.userService = userService;
    }

    public UserExt getActionUser(Long userId) {
        UserExt user = getCurrentUser();
        if(!user.getId().equals(userId)) {
            user = getUserById(userId);
        }
        return user;
    }

    public UserExt getCurrentUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Current user could not be found"));
    }

    public UserExt getUserById(Long userId) {
        return userService.findById(userId).orElseThrow(
            () -> new NotAllowedOperationException("user", userId)
        );
    }

    public Department getCurrentUserDepartment() {
        return getCurrentUser()
            .getSubdepartment()
            .getDepartment();
    }

    public Long getCurrentUserDepartmentId() {
        return getCurrentUserDepartment().getId();
    }
}
