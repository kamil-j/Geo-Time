package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.geotime.config.Constants;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.UserExtRepository;
import pl.edu.agh.geotime.security.AuthoritiesConstants;
import pl.edu.agh.geotime.service.MailService;
import pl.edu.agh.geotime.service.UserService;
import pl.edu.agh.geotime.web.rest.dto.ext.UserExtDTO;
import pl.edu.agh.geotime.web.rest.mapper.UserMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.errors.EmailAlreadyUsedException;
import pl.edu.agh.geotime.web.rest.errors.LoginAlreadyUsedException;
import pl.edu.agh.geotime.web.rest.helper.UserUploadHelper;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    private static final String ENTITY_NAME = "userExt";

    private final Logger log = LoggerFactory.getLogger(UserResource.class);
    private final UserExtRepository userExtRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final MailService mailService;
    private final UserUploadHelper userUploadHelper;

    UserResource(UserExtRepository userExtRepository, UserMapper userMapper, UserService userService,
                 MailService mailService, UserUploadHelper userUploadHelper) {
        this.userExtRepository = userExtRepository;
        this.userMapper = userMapper;
        this.userService = userService;
        this.mailService = mailService;
        this.userUploadHelper = userUploadHelper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<UserExtDTO> createUser(@Valid @RequestBody UserExtDTO userExtDTO) throws URISyntaxException {
        log.debug("REST request to save User: {}", userExtDTO);
        if (userExtDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        } else if (userExtDTO.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("Not allowed to create user with 'ADMIN' authority", ENTITY_NAME, "notallowed");
        } else if (userExtRepository.findOneByUser_Login(userExtDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userExtRepository.findOneByUser_EmailIgnoreCase(userExtDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        }

        UserExt newUser = userService.createUser(userMapper.toEntity(userExtDTO));
        mailService.sendCreationEmail(newUser.getUser());

        return ResponseEntity.created(new URI("/api/users/" + newUser.getUser().getLogin()))
            .headers(HeaderUtil.createAlert( "userManagement.created", newUser.getUser().getLogin()))
            .body(userMapper.toDto(newUser));
    }

    @PutMapping
    @Timed
    public ResponseEntity<UserExtDTO> updateUser(@Valid @RequestBody UserExtDTO userExtDTO) {
        log.debug("REST request to update User: {}", userExtDTO);
        if (userExtDTO.getId() == null) {
            throw new BadRequestAlertException("Cannot update user without ID", ENTITY_NAME, "notallowed");
        }
        if (userExtDTO.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("Not allowed to set 'ADMIN' authority to user", ENTITY_NAME, "notallowed");
        }
        Optional<UserExt> existingUser = userExtRepository.findOneByUser_EmailIgnoreCase(userExtDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userExtDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userExtRepository.findOneByUser_Login(userExtDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userExtDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        UserExt updatedUser = userService.updateUser(userMapper.toEntity(userExtDTO));

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("userManagement.updated", updatedUser.getUser().getLogin()))
            .body(userMapper.toDto(updatedUser));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<UserExtDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get a page of Users");
        final Page<UserExtDTO> page = userService.getAllManagedUsers(pageable)
            .map(userMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<UserExtDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLogin(login)
                .map(userMapper::toDto)
        );
    }

    @DeleteMapping("/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User : {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "userManagement.deleted", login)).build();
    }

    @GetMapping("/authorities")
    @Timed
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    @PostMapping("/upload")
    @Timed
    public ResponseEntity<Void> uploadFileWithUsers(@RequestParam MultipartFile file) throws IOException {
        log.debug("Handling upload file with users.");

        if (file.isEmpty()) {
            log.error("Uploaded file is empty - fileName: {}", file.getName());
            throw new BadRequestAlertException("Uploaded file is empty - fileName: " + file.getName(),
                ENTITY_NAME, "fileEmpty");
        }

        userUploadHelper.uploadUsersFromFile(file);

        log.debug("File uploaded successfully");
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert( "userManagement.uploaded", null))
            .build();
    }
}
