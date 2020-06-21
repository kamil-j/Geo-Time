package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.repository.SemesterRepository;

import java.util.Optional;

@Service
@Transactional
public class SemesterService {

    private final Logger log = LoggerFactory.getLogger(SemesterService.class);

    private final SemesterRepository semesterRepository;
    private final ClassUnitService classUnitService;
    private final CacheManager cacheManager;

    @Autowired
    public SemesterService(SemesterRepository semesterRepository, ClassUnitService classUnitService,
                           CacheManager cacheManager) {
        this.semesterRepository = semesterRepository;
        this.classUnitService = classUnitService;
        this.cacheManager = cacheManager;
    }

    public Semester save(Semester semester) {
        log.debug("Request to save Semester: {}", semester);

        if(semester.isActive()) {
            semesterRepository.findOneByActiveTrue().ifPresent(activeSemester -> {
                if(!activeSemester.getId().equals(semester.getId())) {
                    activeSemester.setActive(false);
                    semesterRepository.save(activeSemester);
                }
            });
        }
        Semester savedSemester = semesterRepository.save(semester);
        cacheManager.getCache(SemesterRepository.ACTIVE_SEMESTER_CACHE).clear();

        classUnitService.recalculateOnlyHalfSemesterInd(savedSemester.getId());
        return savedSemester;
    }

    @Transactional(readOnly = true)
    public Page<Semester> findAll(Pageable pageable) {
        log.debug("Request to get all Semesters");
        return semesterRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Semester> findById(Long id) {
        log.debug("Request to get Semester by id: {}", id);
        return semesterRepository.findById(id);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete Semester by id: {}", id);
        semesterRepository.delete(id);
        cacheManager.getCache(SemesterRepository.ACTIVE_SEMESTER_CACHE).clear();
    }

    public Optional<Semester> getCurrentSemester() {
        log.debug("Request to get current semester");
        return semesterRepository.findOneByActiveTrue();
    }
}
