package pl.edu.agh.geotime.web.rest.util;

import io.github.jhipster.service.filter.LongFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Collection;

public class SpecificationBuilder  {

    private SpecificationBuilder() {
    }

    public static Predicate buildSpecification(LongFilter filter, Path<Long> fieldPath, CriteriaBuilder builder) {
        if (filter.getEquals() != null) {
            return equalsPredicate(filter.getEquals(), fieldPath, builder);
        } else if (filter.getIn() != null) {
            return valueInPredicate(filter.getIn(), fieldPath, builder);
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(filter.getSpecified(), fieldPath, builder);
        }
        return null;
    }

    private static Predicate equalsPredicate(Long value, Path<Long> fieldPath, CriteriaBuilder builder) {
        return builder.equal(fieldPath, value);
    }

    private static Predicate valueInPredicate(final Collection<Long> values, Path<Long> fieldPath, CriteriaBuilder builder) {
        CriteriaBuilder.In<Long> inBuilder = builder.in(fieldPath);
        for (Long value : values) {
            inBuilder = inBuilder.value(value);
        }
        return inBuilder;
    }

    private static Predicate byFieldSpecified(final boolean specified, Path<Long> fieldPath, CriteriaBuilder builder) {
        return specified ? builder.isNotNull(fieldPath) : builder.isNull(fieldPath);
    }
}
