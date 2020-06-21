package pl.edu.agh.geotime.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import pl.edu.agh.geotime.domain.*; // for static metamodels
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.service.criteria.AcademicUnitCriteria;
import pl.edu.agh.geotime.service.dto.AcademicUnitInfoDTO;

@Service
@Transactional(readOnly = true)
public class AcademicUnitQueryService extends QueryService<AcademicUnit> {

    private final Logger log = LoggerFactory.getLogger(AcademicUnitQueryService.class);

    private final AcademicUnitRepository academicUnitRepository;

    public AcademicUnitQueryService(AcademicUnitRepository academicUnitRepository) {
        this.academicUnitRepository = academicUnitRepository;
    }

    @Transactional(readOnly = true)
    public List<AcademicUnitInfoDTO> findByCriteria(AcademicUnitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<AcademicUnit> specification = createSpecification(criteria);
        return academicUnitRepository.findAll(specification).stream()
            .map(AcademicUnitInfoDTO::new)
            .collect(Collectors.toList());
    }

    private Specifications<AcademicUnit> createSpecification(AcademicUnitCriteria criteria) {
        Specifications<AcademicUnit> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), AcademicUnit_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), AcademicUnit_.name));
            }
            if (criteria.getYear() != null) {
                specification = specification.and(buildSpecification(criteria.getYear(), AcademicUnit_.year));
            }
            if (criteria.getDegree() != null) {
                specification = specification.and(buildSpecification(criteria.getDegree(), AcademicUnit_.degree));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), AcademicUnit_.description));
            }
            if (criteria.getStudyFieldId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStudyFieldId(), AcademicUnit_.studyField, StudyField_.id));
            }
            if (criteria.getClassUnitId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getClassUnitId(), AcademicUnit_.classUnits, ClassUnit_.id));
            }
        }
        return specification;
    }
}
