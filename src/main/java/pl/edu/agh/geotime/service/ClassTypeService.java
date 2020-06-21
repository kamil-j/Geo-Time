package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassType;
import pl.edu.agh.geotime.repository.ClassTypeRepository;

import java.util.Optional;

@Service
@Transactional
public class ClassTypeService {

    private final Logger log = LoggerFactory.getLogger(ClassTypeService.class);
    private final ClassTypeRepository classTypeRepository;

    public ClassTypeService(ClassTypeRepository classTypeRepository) {
        this.classTypeRepository = classTypeRepository;
    }

    public ClassType save(ClassType classType) {
        log.debug("Request to save ClassType : {}", classType);
        return classTypeRepository.save(classType);
    }

    @Transactional(readOnly = true)
    public Page<ClassType> findAll(Pageable pageable) {
        log.debug("Request to get all ClassTypes");
        return classTypeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<ClassType> findById(Long id) {
        log.debug("Request to get ClassType by id: {}", id);
        return classTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ClassType> findByShortName(String shortName) {
        log.debug("Request to get ClassType by shortName: {}", shortName);
        return classTypeRepository.findByShortName(shortName);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete ClassType by id: {}", id);
        classTypeRepository.delete(id);
    }
}
