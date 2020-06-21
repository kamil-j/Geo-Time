package pl.edu.agh.geotime.service.query;

import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.LongFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.service.criteria.ClassUnitCriteria;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;

@Service
@Transactional(readOnly = true)
public class ClassUnitQueryService extends QueryService<ClassUnit> {

    private final Logger log = LoggerFactory.getLogger(ClassUnitQueryService.class);

    private final ClassUnitRepository classUnitRepository;
    private final UserHelper userHelper;

    public ClassUnitQueryService(ClassUnitRepository classUnitRepository, UserHelper userHelper) {
        this.classUnitRepository = classUnitRepository;
        this.userHelper = userHelper;
    }

    @Transactional(readOnly = true)
    public Page<ClassUnit> findByCriteria(ClassUnitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ClassUnit> specification = createSpecification(criteria);
        return classUnitRepository.findAll(specification, page);
    }

    private Specifications<ClassUnit> createSpecification(ClassUnitCriteria criteria) {
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return createSpecificationWithDepartmentId(criteria, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return createSpecificationWithSubdepartmentId(criteria, subdepartmentId);
    }

    private Specifications<ClassUnit> createSpecificationWithDepartmentId(ClassUnitCriteria criteria, Long departmentId) {
        Specifications<ClassUnit> specification = createCommonSpecification(criteria);
        if(departmentId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get(ClassUnit_.subdepartment)
                .get(Subdepartment_.department).get(Department_.id), departmentId));
        }
        return specification;
    }

    private Specifications<ClassUnit> createSpecificationWithSubdepartmentId(ClassUnitCriteria criteria, Long subdepartmentId) {
        LongFilter subdepartmentIdFilter = new LongFilter();
        subdepartmentIdFilter.setEquals(subdepartmentId);
        criteria.setSubdepartmentId(subdepartmentIdFilter);

        return createCommonSpecification(criteria);
    }

    /**
     * Function to convert ClassUnitCriteria to a {@link Specifications}
     */
    private Specifications<ClassUnit> createCommonSpecification(ClassUnitCriteria criteria) {
        Specifications<ClassUnit> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ClassUnit_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), ClassUnit_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ClassUnit_.description));
            }
            if (criteria.getDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDuration(), ClassUnit_.duration));
            }
            if (criteria.getHoursQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHoursQuantity(), ClassUnit_.hoursQuantity));
            }
            if (criteria.getFrequency() != null) {
                specification = specification.and(buildSpecification(criteria.getFrequency(), ClassUnit_.frequency));
            }
            if (criteria.getAcademicUnitGroup() != null) {
                specification = specification.and(buildSpecification(criteria.getAcademicUnitGroup(), ClassUnit_.academicUnitGroup));
            }
            if (criteria.getOnlySemesterHalf() != null) {
                specification = specification.and(buildSpecification(criteria.getOnlySemesterHalf(), ClassUnit_.onlySemesterHalf));
            }
            if (criteria.getClassTypeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getClassTypeId(), ClassUnit_.classType, ClassType_.id));
            }
            if (criteria.getUserExtId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserExtId(), ClassUnit_.userExt, UserExt_.id));
            }
            if (criteria.getRoomId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getRoomId(), ClassUnit_.rooms, Room_.id));
            }
            if (criteria.getBookingUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getBookingUnitId(), ClassUnit_.bookingUnit, BookingUnit_.id));
            }
            if (criteria.getScheduleUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getScheduleUnitId(), ClassUnit_.scheduleUnits, ScheduleUnit_.id));
            }
            if (criteria.getSemesterId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSemesterId(), ClassUnit_.semester, Semester_.id));
            }
            if (criteria.getAcademicUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getAcademicUnitId(), ClassUnit_.academicUnit, AcademicUnit_.id));
            }
            if (criteria.getClassUnitGroupId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getClassUnitGroupId(), ClassUnit_.classUnitGroup, ClassUnitGroup_.id));
            }
            if (criteria.getSubdepartmentId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSubdepartmentId(), ClassUnit_.subdepartment, Subdepartment_.id));
            }
        }
        return specification;
    }
}
