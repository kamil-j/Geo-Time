package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.UserGroup;
import pl.edu.agh.geotime.repository.UserGroupRepository;

import java.util.Optional;

@Service
@Transactional
public class UserGroupService {

    private final Logger log = LoggerFactory.getLogger(UserGroupService.class);
    private final UserGroupRepository userGroupRepository;

    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public UserGroup save(UserGroup userGroup) {
        log.debug("Request to save UserGroup: {}", userGroup);
        return userGroupRepository.save(userGroup);
    }

    @Transactional(readOnly = true)
    public Page<UserGroup> findAll(Pageable pageable) {
        log.debug("Request to get all UserGroups");
        return userGroupRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<UserGroup> findById(Long id) {
        log.debug("Request to get UserGroup by id: {}", id);
        return userGroupRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserGroup> findByName(String name) {
        log.debug("Request to get UserGroup by name: {}", name);
        return userGroupRepository.findByName(name);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete UserGroup by id: {}", id);
        userGroupRepository.delete(id);
    }
}
