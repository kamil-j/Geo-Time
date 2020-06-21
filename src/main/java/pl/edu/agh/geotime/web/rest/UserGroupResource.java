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
import pl.edu.agh.geotime.domain.UserGroup;
import pl.edu.agh.geotime.service.UserGroupService;
import pl.edu.agh.geotime.web.rest.dto.UserGroupDTO;
import pl.edu.agh.geotime.web.rest.mapper.UserGroupMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-groups")
public class UserGroupResource {

    private static final String ENTITY_NAME = "userGroup";

    private final Logger log = LoggerFactory.getLogger(UserGroupResource.class);
    private final UserGroupService userGroupService;
    private final UserGroupMapper userGroupMapper;

    UserGroupResource(UserGroupService userGroupService, UserGroupMapper userGroupMapper) {
        this.userGroupService = userGroupService;
        this.userGroupMapper = userGroupMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<UserGroupDTO> createUserGroup(@Valid @RequestBody UserGroupDTO userGroupDTO) throws URISyntaxException {
        log.debug("REST request to save UserGroup: {}", userGroupDTO);
        if (userGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new userGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserGroup result = userGroupService.save(userGroupMapper.toEntity(userGroupDTO));
        return ResponseEntity.created(new URI("/api/user-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(userGroupMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<UserGroupDTO> updateUserGroup(@Valid @RequestBody UserGroupDTO userGroupDTO) throws URISyntaxException {
        log.debug("REST request to update UserGroup: {}", userGroupDTO);
        if (userGroupDTO.getId() == null) {
            return createUserGroup(userGroupDTO);
        }
        UserGroup result = userGroupService.save(userGroupMapper.toEntity(userGroupDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userGroupDTO.getId().toString()))
            .body(userGroupMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<UserGroupDTO>> getAllUserGroups(Pageable pageable) {
        log.debug("REST request to get a page of UserGroups");
        Page<UserGroupDTO> page = userGroupService.findAll(pageable)
            .map(userGroupMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-groups");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<UserGroupDTO> getUserGroup(@PathVariable Long id) {
        log.debug("REST request to get UserGroup: {}", id);
        Optional<UserGroupDTO> userGroupDTO = userGroupService.findById(id)
            .map(userGroupMapper::toDto);
        return ResponseUtil.wrapOrNotFound(userGroupDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserGroup(@PathVariable Long id) {
        log.debug("REST request to delete UserGroup: {}", id);
        userGroupService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
