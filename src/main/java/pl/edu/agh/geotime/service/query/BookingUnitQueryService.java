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
import pl.edu.agh.geotime.repository.BookingUnitRepository;
import pl.edu.agh.geotime.service.criteria.BookingUnitCriteria;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;
import pl.edu.agh.geotime.web.rest.util.SpecificationBuilder;

import javax.persistence.criteria.Path;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookingUnitQueryService extends QueryService<BookingUnit> {

    private static final Logger log = LoggerFactory.getLogger(BookingUnitQueryService.class);

    private final BookingUnitRepository bookingUnitRepository;
    private final UserHelper userHelper;

    public BookingUnitQueryService(BookingUnitRepository bookingUnitRepository, UserHelper userHelper) {
        this.bookingUnitRepository = bookingUnitRepository;
        this.userHelper = userHelper;
    }

    @Transactional(readOnly = true)
    public List<BookingUnit> findByCriteria(BookingUnitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<BookingUnit> specification = createCommonSpecification(criteria);
        return bookingUnitRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<BookingUnit> findByCriteria(BookingUnitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<BookingUnit> specification = createSpecification(criteria);
        return bookingUnitRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public List<BookingUnit> findByCriteriaAndAcademicUnitIdIn(BookingUnitCriteria criteria, Set<Long> academicUnitIds) {
        log.debug("Getting bookingUnits by criteria: {} and academic unit ids: {}", criteria, academicUnitIds);
        Specifications<BookingUnit> specification = createCommonSpecification(criteria);
        if(academicUnitIds != null && !academicUnitIds.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get(BookingUnit_.classUnit).get(ClassUnit_.academicUnit)
                .get(AcademicUnit_.id).in(academicUnitIds));
        }

        return bookingUnitRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public List<BookingUnit> findByCriteriaAndUserIdIn(BookingUnitCriteria criteria, Set<Long> userIds) {
        log.debug("Getting bookingUnits by criteria: {} and user ids: {}", criteria, userIds);
        Specifications<BookingUnit> specification = createCommonSpecification(criteria);
        if(userIds != null && !userIds.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get(BookingUnit_.classUnit).get(ClassUnit_.userExt)
                .get(UserExt_.id).in(userIds));
        }

        return bookingUnitRepository.findAll(specification);
    }

    private Specifications<BookingUnit> createSpecification(BookingUnitCriteria criteria) {
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return createSpecificationWithDepartmentId(criteria, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return createSpecificationWithSubdepartmentId(criteria, subdepartmentId);
    }

    private Specifications<BookingUnit> createSpecificationWithDepartmentId(BookingUnitCriteria criteria, Long departmentId) {
        Specifications<BookingUnit> specification = createCommonSpecification(criteria);
        if(departmentId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get(BookingUnit_.classUnit)
                .get(ClassUnit_.subdepartment).get(Subdepartment_.department).get(Department_.id), departmentId));
        }
        return specification;
    }

    private Specifications<BookingUnit> createSpecificationWithSubdepartmentId(BookingUnitCriteria criteria, Long subdepartmentId) {
        Specifications<BookingUnit> specification = createCommonSpecification(criteria);
        if(subdepartmentId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get(BookingUnit_.classUnit)
                .get(ClassUnit_.subdepartment).get(Subdepartment_.id), subdepartmentId));
        }
        return specification;
    }

    /**
     * Function to convert BookingUnitCriteria to a {@link Specifications}
     */
    private Specifications<BookingUnit> createCommonSpecification(BookingUnitCriteria criteria) {
        Specifications<BookingUnit> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), BookingUnit_.id));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDateTime(), BookingUnit_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDateTime(), BookingUnit_.endTime));
            }
            if (criteria.getDay() != null) {
                specification = specification.and(buildSpecification(criteria.getDay(), BookingUnit_.day));
            }
            if (criteria.getWeek() != null) {
                specification = specification.and(buildSpecification(criteria.getWeek(), BookingUnit_.week));
            }
            if (criteria.getSemesterHalf() != null) {
                specification = specification.and(buildSpecification(criteria.getSemesterHalf(), BookingUnit_.semesterHalf));
            }
            if (criteria.getLocked() != null) {
                specification = specification.and(buildSpecification(criteria.getLocked(), BookingUnit_.locked));
            }
            if (criteria.getClassUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getClassUnitId(), BookingUnit_.classUnit, ClassUnit_.id));
            }
            if (criteria.getRoomId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getRoomId(), BookingUnit_.room, Room_.id));
            }
            if (criteria.getSemesterIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(BookingUnit_.classUnit).get(ClassUnit_.semester)
                    .get(Semester_.id), criteria.getSemesterIdEquals()));
            }
            if (criteria.getAcademicUnitIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(BookingUnit_.classUnit).get(ClassUnit_.academicUnit)
                    .get(AcademicUnit_.id), criteria.getAcademicUnitIdEquals()));
            }
            if (criteria.getUserIdNotEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.notEqual(root.get(BookingUnit_.classUnit).get(ClassUnit_.userExt)
                    .get(UserExt_.id), criteria.getUserIdNotEquals()));
            }
            if (criteria.getUserIdEquals() != null) {
                specification = specification.and((root, query, cb) -> cb.equal(root.get(BookingUnit_.classUnit).get(ClassUnit_.userExt)
                    .get(UserExt_.id), criteria.getUserIdEquals()));
            }
            if (criteria.getSubdepartmentId() != null) {
                specification = specification.and((root, query, builder) -> {
                    Path<Long> fieldPath = root.get(BookingUnit_.classUnit).get(ClassUnit_.subdepartment).get(Subdepartment_.id);
                    return SpecificationBuilder.buildSpecification(criteria.getSubdepartmentId(), fieldPath, builder);
                });
            }
        }
        return specification;
    }
}
