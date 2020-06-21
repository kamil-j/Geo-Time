package pl.edu.agh.geotime.service.criteria;

import java.io.Serializable;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the AcademicUnit entity. This class is used in AcademicUnitResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /academic-units?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AcademicUnitCriteria implements Serializable {
    /**
     * Class for filtering AcademicUnitYear
     */
    public static class AcademicUnitYearFilter extends Filter<AcademicUnitYear> {
    }

    /**
     * Class for filtering AcademicUnitDegree
     */
    public static class AcademicUnitDegreeFilter extends Filter<AcademicUnitDegree> {
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter name;
    private AcademicUnitYearFilter year;
    private AcademicUnitDegreeFilter degree;
    private StringFilter description;
    private LongFilter studyFieldId;
    private LongFilter classUnitId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public AcademicUnitYearFilter getYear() {
        return year;
    }

    public void setYear(AcademicUnitYearFilter year) {
        this.year = year;
    }

    public AcademicUnitDegreeFilter getDegree() {
        return degree;
    }

    public void setDegree(AcademicUnitDegreeFilter degree) {
        this.degree = degree;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getStudyFieldId() {
        return studyFieldId;
    }

    public void setStudyFieldId(LongFilter studyFieldId) {
        this.studyFieldId = studyFieldId;
    }

    public LongFilter getClassUnitId() {
        return classUnitId;
    }

    public void setClassUnitId(LongFilter classUnitId) {
        this.classUnitId = classUnitId;
    }

    @Override
    public String toString() {
        return "AcademicUnitCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (year != null ? "year=" + year + ", " : "") +
                (degree != null ? "degree=" + degree + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (studyFieldId != null ? "studyFieldId=" + studyFieldId + ", " : "") +
                (classUnitId != null ? "classUnitId=" + classUnitId + ", " : "") +
            "}";
    }
}
