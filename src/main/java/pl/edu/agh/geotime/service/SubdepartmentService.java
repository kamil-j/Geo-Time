package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.SubdepartmentRepository;
import pl.edu.agh.geotime.service.errors.NotAllowedOperationException;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;

import java.util.Optional;

@Service
@Transactional
public class SubdepartmentService {

    private final Logger log = LoggerFactory.getLogger(SubdepartmentService.class);

    private final SubdepartmentRepository subdepartmentRepository;
    private final UserHelper userHelper;

    public SubdepartmentService(SubdepartmentRepository subdepartmentRepository, UserHelper userHelper) {
        this.subdepartmentRepository = subdepartmentRepository;
        this.userHelper = userHelper;
    }

    public Subdepartment save(Subdepartment subdepartment) {
        log.debug("Request to save Subdepartment: {}", subdepartment);
        subdepartment.setDepartment(userHelper.getCurrentUserDepartment());
        return subdepartmentRepository.save(subdepartment);
    }

    @Transactional(readOnly = true)
    public Page<Subdepartment> findAll(Pageable pageable) {
        log.debug("Request to get all Subdepartments");
        UserExt user = userHelper.getCurrentUser();
        if(!UserUtil.isManager(user)) {
            Long subdepartmentId = user.getSubdepartment().getId();
            return subdepartmentRepository.findAllById(pageable, subdepartmentId);
        }
        Long departmentId = user.getSubdepartment().getDepartment().getId();
        return subdepartmentRepository.findAllByDepartmentId(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Subdepartment> findById(Long id) {
        log.debug("Request to get Subdepartment: {}", id);
        UserExt user = userHelper.getCurrentUser();
        if(!user.getSubdepartment().getId().equals(id) && !UserUtil.isManager(user)) {
            throw new NotAllowedOperationException("subdepartment", id);
        }
        Long departmentId = user.getSubdepartment().getDepartment().getId();
        return subdepartmentRepository.findByIdAndDepartmentId(id, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<Subdepartment> findByShortName(String shortName) {
        log.debug("Request to get Subdepartment by shortName: {}", shortName);
        UserExt user = userHelper.getCurrentUser();
        if(!user.getSubdepartment().getShortName().equals(shortName) && !UserUtil.isManager(user)) {
            throw new NotAllowedOperationException("subdepartment", "shortName", shortName);
        }
        Long departmentId = user.getSubdepartment().getDepartment().getId();
        return subdepartmentRepository.findByShortNameAndDepartmentId(shortName, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete Subdepartment: {}", id);
        UserExt user = userHelper.getCurrentUser();
        if(!user.getSubdepartment().getId().equals(id) && !UserUtil.isManager(user)) {
            throw new NotAllowedOperationException("subdepartment", id);
        }
        Long departmentId = user.getSubdepartment().getDepartment().getId();
        subdepartmentRepository.deleteByIdAndDepartmentId(id, departmentId);
    }
}
