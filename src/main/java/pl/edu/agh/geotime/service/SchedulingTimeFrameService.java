package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.repository.SchedulingTimeFrameRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.util.Optional;

@Service
@Transactional
public class SchedulingTimeFrameService {

    private final Logger log = LoggerFactory.getLogger(SchedulingTimeFrameService.class);
    private final DepartmentAccessService departmentAccessService;
    private final SchedulingTimeFrameRepository schedulingTimeFrameRepository;
    private final UserHelper userHelper;

    public SchedulingTimeFrameService(DepartmentAccessService departmentAccessService,
                                      SchedulingTimeFrameRepository schedulingTimeFrameRepository, UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.schedulingTimeFrameRepository = schedulingTimeFrameRepository;
        this.userHelper = userHelper;
    }

    public SchedulingTimeFrame save(SchedulingTimeFrame schedulingTimeFrame) {
        log.debug("Request to save SchedulingTimeFrame: {}", schedulingTimeFrame);
        departmentAccessService.checkAccess(schedulingTimeFrame);
        return schedulingTimeFrameRepository.save(schedulingTimeFrame);
    }

    @Transactional(readOnly = true)
    public Page<SchedulingTimeFrame> findAll(Pageable pageable) {
        log.debug("Request to get all SchedulingTimeFrame");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return schedulingTimeFrameRepository.findAllBySubdepartment_Department_Id(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<SchedulingTimeFrame> findById(Long id) {
        log.debug("Request to get SchedulingTimeFrame by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return schedulingTimeFrameRepository.findByIdAndSubdepartment_Department_Id(id, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete SchedulingTimeFrame by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        schedulingTimeFrameRepository.deleteByIdAndSubdepartment_Department_Id(id, departmentId);
    }
}
