package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    private final Logger log = LoggerFactory.getLogger(DepartmentService.class);
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department save(Department department) {
        log.debug("Request to save Department : {}", department);
        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public Page<Department> findAll(Pageable pageable) {
        log.debug("Request to get all Departments");
        return departmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Department> findById(Long id) {
        log.debug("Request to get Department by id: {}", id);
        return departmentRepository.findById(id);
    }

    public void delete(Long id) {
        log.debug("Request to delete Department by id: {}", id);
        departmentRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Department> findAllNotFunctional() {
        log.debug("Request to get all not functional Departments");
        return departmentRepository.findAllByFunctionalIsFalse();
    }

    @Transactional(readOnly = true)
    public Optional<Department> findNotFunctionalById(Long id) {
        log.debug("Request to get not functional Department by id: {}", id);
        return departmentRepository.findOneByIdAndFunctionalIsFalse(id);
    }
}
