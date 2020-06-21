package pl.edu.agh.geotime.service.query;

import io.github.jhipster.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.repository.ScheduleUnitRepository;
import pl.edu.agh.geotime.service.criteria.ScheduleUnitCriteria;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;
import pl.edu.agh.geotime.web.rest.util.SpecificationBuilder;

import javax.persistence.criteria.Path;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScheduleUnitQueryService extends QueryService<ScheduleUnit> {

    private final Logger log = LoggerFactory.getLogger(ScheduleUnitQueryService.class);

    private final ScheduleUnitRepository scheduleUnitRepository;
    private final UserHelper userHelper;

    public ScheduleUnitQueryService(ScheduleUnitRepository scheduleUnitRepository, UserHelper userHelper) {
        this.scheduleUnitRepository = scheduleUnitRepository;
        this.userHelper = userHelper;
    }

    @Transactional(readOnly = true)
    public List<ScheduleUnit> findByCriteria(ScheduleUnitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<ScheduleUnit> specification = createCommonSpecification(criteria);
        return scheduleUnitRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleUnit> findByCriteria(ScheduleUnitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ScheduleUnit> specification = createSpecification(criteria);
        return scheduleUnitRepository.findAll(specification, page);
    }

    private Specifications<ScheduleUnit> createSpecification(ScheduleUnitCriteria criteria) {
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return createSpecificationWithDepartmentId(criteria, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return createSpecificationWithSubdepartmentId(criteria, subdepartmentId);
    }

    private Specifications<ScheduleUnit> createSpecificationWithDepartmentId(ScheduleUnitCriteria criteria, Long departmentId) {
        Specifications<ScheduleUnit> specification = createCommonSpecification(criteria);
        if(departmentId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get(ScheduleUnit_.classUnit)
                .get(ClassUnit_.subdepartment).get(Subdepartment_.department).get(Department_.id), departmentId));
        }
        return specification;
    }

    private Specifications<ScheduleUnit> createSpecificationWithSubdepartmentId(ScheduleUnitCriteria criteria, Long subdepartmentId) {
        Specifications<ScheduleUnit> specification = createCommonSpecification(criteria);
        if(subdepartmentId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get(ScheduleUnit_.classUnit)
                .get(ClassUnit_.subdepartment).get(Subdepartment_.id), subdepartmentId));
        }
        return specification;
    }

    private Specifications<ScheduleUnit> createCommonSpecification(ScheduleUnitCriteria criteria) {
        Specifications<ScheduleUnit> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ScheduleUnit_.id));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), ScheduleUnit_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), ScheduleUnit_.endDate));
            }
            if (criteria.getClassUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getClassUnitId(), ScheduleUnit_.classUnit, ClassUnit_.id));
            }
            if (criteria.getRoomId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getRoomId(), ScheduleUnit_.room, Room_.id));
            }
            if (criteria.getAcademicUnitGroupEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.or(cb.equal(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.academicUnitGroup)
                    ,criteria.getAcademicUnitGroupEquals()), cb.isNull(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.academicUnitGroup))));
            }
            if (criteria.getSemesterIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.semester)
                    .get(Semester_.id), criteria.getSemesterIdEquals()));
            }
            if (criteria.getAcademicUnitIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.academicUnit)
                    .get(AcademicUnit_.id), criteria.getAcademicUnitIdEquals()));
            }
            if (criteria.getUserIdNotEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.notEqual(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.userExt)
                    .get(UserExt_.id), criteria.getUserIdNotEquals()));
            }
            if (criteria.getUserIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(ScheduleUnit_.classUnit).get(ClassUnit_.userExt)
                    .get(UserExt_.id), criteria.getUserIdEquals()));
            }
            if (criteria.getSubdepartmentId() != null) {
                specification = specification.and((root, query, builder) -> {
                    Path<Long> fieldPath = root.get(ScheduleUnit_.classUnit).get(ClassUnit_.subdepartment).get(Subdepartment_.id);
                    return SpecificationBuilder.buildSpecification(criteria.getSubdepartmentId(), fieldPath, builder);
                });
            }
        }
        return specification;
    }
}
