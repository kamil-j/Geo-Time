package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.config.Constants;
import pl.edu.agh.geotime.domain.Authority;
import pl.edu.agh.geotime.domain.User;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.AuthorityRepository;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.repository.UserRepository;
import pl.edu.agh.geotime.security.AuthoritiesConstants;
import pl.edu.agh.geotime.security.SecurityUtils;
import pl.edu.agh.geotime.service.util.RandomUtil;
import pl.edu.agh.geotime.service.util.UserUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserExtRepository userExtRepository;
    private final DepartmentAccessService departmentAccessService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final CacheManager cacheManager;

    public UserService(UserRepository userRepository, UserExtRepository userExtRepository, DepartmentAccessService departmentAccessService,
                       PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.userExtRepository = userExtRepository;
        this.departmentAccessService = departmentAccessService;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    public UserExt createUser(UserExt userExt) {
        departmentAccessService.checkAccess(userExt);

        User user = userExt.getUser();
        if (user.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        user.setAuthorities(getAuthoritiesEntity(user.getAuthorities()));
        user.setPassword(passwordEncoder.encode(RandomUtil.generatePassword()));
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());

        User savedUser = userRepository.save(user);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
        log.debug("Created Information for User: {}", user);

        userExt.setId(savedUser.getId());
        userExtRepository.save(userExt);
        cacheManager.getCache(UserExtRepository.USERS_EXT_BY_LOGIN_CACHE).evict(user.getLogin());
        log.debug("Created new UserExt entity: {}", userExt);

        return userExt;
    }

    private Set<Authority> getAuthoritiesEntity(Set<Authority> authorities) {
        if(authorities == null) {
            return Collections.emptySet();
        }
        return authorities.stream()
            .map(Authority::getName)
            .map(authorityRepository::findOne)
            .collect(Collectors.toSet());
    }

    public UserExt updateUser(UserExt userExt) {
        log.debug("Updating user info: {}", userExt);
        departmentAccessService.checkAccess(userExt);

        User user = userExt.getUser();
        UserExt existingUserExt = findById(userExt.getId())
            .orElseThrow(() -> new AccessDeniedException("User not exists!"));

        User existingUser = existingUserExt.getUser();
        existingUser.setLogin(user.getLogin());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setActivated(user.getActivated());
        existingUser.setLangKey(user.getLangKey());
        existingUser.setLangKey(existingUser.getLangKey() != null ? existingUser.getLangKey() : Constants.DEFAULT_LANGUAGE);
        existingUser.setAuthorities(getAuthoritiesEntity(user.getAuthorities()));

        userRepository.save(existingUser);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(existingUser.getLogin());
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(existingUser.getEmail());
        log.debug("Changed Information for User: {}", user);

        existingUserExt.setSubdepartment(userExt.getSubdepartment());
        existingUserExt.setUserGroup(userExt.getUserGroup());

        userExtRepository.save(existingUserExt);
        cacheManager.getCache(UserExtRepository.USERS_EXT_BY_LOGIN_CACHE).evict(existingUser.getLogin());
        log.debug("Changed Information for UserExt: {}", userExt);

        return userExt;
    }

    @Transactional(readOnly = true)
    public Optional<UserExt> findById(Long id) {
        log.debug("Request to get userExt by id: {}", id);
        UserExt currentUser = getCurrentUser();

        if (UserUtil.isManager(currentUser)) {
            Long userDepartmentId = currentUser.getSubdepartment().getDepartment().getId();
            return userExtRepository.findOneByIdAndSubdepartment_Department_Id(id, userDepartmentId);
        }
        Long userSubdepartmentId = currentUser.getSubdepartment().getId();
        return userExtRepository.findOneByIdAndSubdepartment_Id(id, userSubdepartmentId);
    }

    private UserExt getCurrentUser() {
        return getUserWithAuthorities()
            .orElseThrow(() -> new RuntimeException("Current user could not be found"));
    }

    @Transactional(readOnly = true)
    public Optional<UserExt> findByIdWithAuthorities(Long id) {
        log.debug("Request to get userExt with authorities by id: {}", id);
        UserExt currentUser = getCurrentUser();

        if (UserUtil.isManager(currentUser)) {
            Long userDepartmentId = currentUser.getSubdepartment().getDepartment().getId();
            return userExtRepository.findOneWithUser_AuthoritiesByIdAndSubdepartment_Department_Id(id, userDepartmentId);
        }
        Long userSubdepartmentId = currentUser.getSubdepartment().getId();
        return userExtRepository.findOneWithUser_AuthoritiesByIdAndSubdepartment_Id(id, userSubdepartmentId);
    }

    @Transactional(readOnly = true)
    public Optional<UserExt> findByLogin(String login) {
        log.debug("Request to get userExt by login: {}", login);
        UserExt currentUser = getCurrentUser();

        if (UserUtil.isManager(currentUser)) {
            Long userDepartmentId = currentUser.getSubdepartment().getDepartment().getId();
            return userExtRepository.findOneByUser_LoginAndSubdepartment_Department_Id(login, userDepartmentId);
        }
        Long userSubdepartmentId = currentUser.getSubdepartment().getId();
        return userExtRepository.findOneByUser_LoginAndSubdepartment_Id(login, userSubdepartmentId);
    }

    @Transactional(readOnly = true)
    public Page<UserExt> getAllManagedUsers(Pageable pageable) {
        UserExt currentUser = getCurrentUser();

        if (UserUtil.isManager(currentUser)) {
            return userExtRepository.findAllBySubdepartment_DepartmentAndUser_AuthoritiesContains(pageable,
                currentUser.getSubdepartment().getDepartment(), new Authority(AuthoritiesConstants.USER));
        }
        return userExtRepository.findAllBySubdepartmentAndUser_AuthoritiesContains(pageable, currentUser.getSubdepartment(),
                new Authority(AuthoritiesConstants.USER));
    }

    public void deleteUser(String login) {
        findByLogin(login).ifPresent(this::removeUser);
    }

    private void removeUser(UserExt userExt) {
        userExtRepository.delete(userExt);
        cacheManager.getCache(UserExtRepository.USERS_EXT_BY_LOGIN_CACHE).evict(userExt.getUser().getLogin());
        log.debug("Deleted UserExt: {}", userExt);

        User user = userExt.getUser();
        userRepository.delete(user);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
        log.debug("Deleted User: {}", user);
    }

    @Transactional(readOnly = true)
    public Optional<UserExt> getUserWithAuthoritiesByLogin(String login) {
        UserExt currentUser = getCurrentUser();

        if(UserUtil.isManager(currentUser)) {
            Long userDepartmentId = currentUser.getSubdepartment().getDepartment().getId();
            return userExtRepository.findOneWithUser_AuthoritiesByUser_LoginAndSubdepartment_Department_Id(login,
                userDepartmentId);
        }
        Long userSubdepartmentId = currentUser.getSubdepartment().getId();
        return userExtRepository.findOneWithUser_AuthoritiesByUser_LoginAndSubdepartment_Id(login, userSubdepartmentId);
    }

    @Transactional(readOnly = true)
    public Optional<UserExt> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userExtRepository::findOneWithUser_AuthoritiesByUser_Login);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<UserExt> usersExt = userExtRepository.findAllByUser_ActivatedIsFalseAndUser_CreatedDateBefore(
            Instant.now().minus(60, ChronoUnit.DAYS)
        );
        for (UserExt userExt : usersExt) {
            User user = userExt.getUser();
            log.debug("Deleting not activated user {}", user.getLogin());
            userExtRepository.findOneByUser_Login(user.getLogin()).ifPresent(this::removeUser);
        }
    }

    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream()
            .map(Authority::getName)
            .filter(name -> !name.equalsIgnoreCase(AuthoritiesConstants.ADMIN))
            .collect(Collectors.toList());
    }
}
