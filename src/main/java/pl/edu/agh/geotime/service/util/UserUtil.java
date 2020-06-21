package pl.edu.agh.geotime.service.util;

import pl.edu.agh.geotime.domain.Authority;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.security.AuthoritiesConstants;

public class UserUtil {

    private UserUtil() {}

    public static boolean hasAuthority(UserExt user, String authority) {
        return user.getUser().getAuthorities().stream()
            .map(Authority::getName)
            .anyMatch(authority::equals);
    }

    public static boolean isAdmin(UserExt user) {
        return hasAuthority(user, AuthoritiesConstants.ADMIN);
    }

    public static boolean isManager(UserExt user) {
        return hasAuthority(user, AuthoritiesConstants.MANAGER);
    }

    public static boolean isPlanner(UserExt user) {
        return hasAuthority(user, AuthoritiesConstants.PLANNER);
    }

    public static boolean hasAccessToUserAsManager(UserExt manager, UserExt user) {
        return UserUtil.isManager(manager) && UserUtil.haveSameDepartment(manager, user);
    }

    public static boolean hasAccessToUserAsPlanner(UserExt planner, UserExt user) {
        return UserUtil.isPlanner(planner) && UserUtil.haveSameSubdepartment(planner, user);
    }

    private static boolean haveSameDepartment(UserExt first, UserExt second) {
        return first.getSubdepartment().getDepartment().equals(second.getSubdepartment().getDepartment());
    }

    private static boolean haveSameSubdepartment(UserExt first, UserExt second) {
        return first.getSubdepartment().equals(second.getSubdepartment());
    }
}
