package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.geotime.domain.User;
import pl.edu.agh.geotime.repository.UserRepository;
import pl.edu.agh.geotime.security.SecurityUtils;
import pl.edu.agh.geotime.service.MailService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.service.AccountService;
import pl.edu.agh.geotime.web.rest.dto.UserDTO;
import pl.edu.agh.geotime.web.rest.dto.ext.UserExtDTO;
import pl.edu.agh.geotime.web.rest.mapper.UserMapper;
import pl.edu.agh.geotime.web.rest.errors.EmailAlreadyUsedException;
import pl.edu.agh.geotime.web.rest.errors.EmailNotFoundException;
import pl.edu.agh.geotime.web.rest.errors.InternalServerErrorException;
import pl.edu.agh.geotime.web.rest.errors.InvalidPasswordException;
import pl.edu.agh.geotime.web.rest.vm.KeyAndPasswordVM;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final MailService mailService;

    AccountResource(UserRepository userRepository, AccountService accountService, UserService userService,
                    UserMapper userMapper, MailService mailService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.mailService = mailService;
    }

    @GetMapping("/activate")
    @Timed
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = accountService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    @GetMapping("/account")
    @Timed
    public UserExtDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(userMapper::toDto)
            .orElseThrow(() -> new InternalServerErrorException("User could not be found"));
    }

    @PostMapping("/account")
    @Timed
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new InternalServerErrorException("User could not be found");
        }
        accountService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey());
   }

    @PostMapping(path = "/account/change-password")
    @Timed
    public void changePassword(@RequestBody String password) {
        if (isInvalidPasswordLength(password)) {
            throw new InvalidPasswordException();
        }
        accountService.changePassword(password);
   }

    @PostMapping(path = "/account/reset-password/init")
    @Timed
    public void requestPasswordReset(@RequestBody String mail) {
       mailService.sendPasswordResetMail(
           accountService.requestPasswordReset(mail)
               .orElseThrow(EmailNotFoundException::new)
       );
    }

    @PostMapping(path = "/account/reset-password/finish")
    @Timed
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isInvalidPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            accountService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new InternalServerErrorException("No user was found for this reset key");
        }
    }

    private static boolean isInvalidPasswordLength(String password) {
        return StringUtils.isEmpty(password) || password.length() < 4 || password.length() > 100;
    }
}
