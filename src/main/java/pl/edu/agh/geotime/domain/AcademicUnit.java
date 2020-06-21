package pl.edu.agh.geotime.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;

import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;

@Entity
@Table(name = "academic_units",
    uniqueConstraints={@UniqueConstraint(columnNames = {"year", "degree", "study_field_id"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AcademicUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "year", nullable = false)
    private AcademicUnitYear year;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "degree", nullable = false)
    private AcademicUnitDegree degree;

    @Size(max = 50)
    @Column(name = "description", length = 50)
    private String description;

    @ManyToOne(optional = false)
    @NotNull
    private StudyField studyField;

    @OneToMany(mappedBy = "academicUnit", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public AcademicUnit name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcademicUnitYear getYear() {
        return year;
    }

    public AcademicUnit year(AcademicUnitYear year) {
        this.year = year;
        return this;
    }

    public void setYear(AcademicUnitYear year) {
        this.year = year;
    }

    public AcademicUnitDegree getDegree() {
        return degree;
    }

    public AcademicUnit degree(AcademicUnitDegree degree) {
        this.degree = degree;
        return this;
    }

    public void setDegree(AcademicUnitDegree degree) {
        this.degree = degree;
    }

    public String getDescription() {
        return description;
    }

    public AcademicUnit description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StudyField getStudyField() {
        return studyField;
    }

    public AcademicUnit studyField(StudyField studyField) {
        this.studyField = studyField;
        return this;
    }

    public void setStudyField(StudyField studyField) {
        this.studyField = studyField;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public AcademicUnit classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public AcademicUnit addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setAcademicUnit(this);
        return this;
    }

    public AcademicUnit removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setAcademicUnit(null);
        return this;
    }

    public void setClassUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AcademicUnit academicUnit = (AcademicUnit) o;
        if (academicUnit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), academicUnit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AcademicUnit{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", year='" + getYear() + "'" +
            ", degree='" + getDegree() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
