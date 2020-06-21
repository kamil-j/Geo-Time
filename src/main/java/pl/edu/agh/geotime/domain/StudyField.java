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

@Entity
@Table(name = "study_fields",
    uniqueConstraints={
        @UniqueConstraint(columnNames = {"name", "department_id"}),
        @UniqueConstraint(columnNames = {"short_name", "department_id"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StudyField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 10)
    @Column(name = "short_name", length = 10, nullable = false)
    private String shortName;

    @ManyToOne(optional = false)
    @NotNull
    private Department department;

    @OneToMany(mappedBy = "studyField", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<AcademicUnit> academicUnits = new HashSet<>();

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

    public StudyField name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public StudyField shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Department getDepartment() {
        return department;
    }

    public StudyField department(Department department) {
        this.department = department;
        return this;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<AcademicUnit> getAcademicUnits() {
        return academicUnits;
    }

    public StudyField academicUnits(Set<AcademicUnit> academicUnits) {
        this.academicUnits = academicUnits;
        return this;
    }

    public StudyField addAcademicUnit(AcademicUnit academicUnit) {
        this.academicUnits.add(academicUnit);
        academicUnit.setStudyField(this);
        return this;
    }

    public StudyField removeAcademicUnit(AcademicUnit academicUnit) {
        this.academicUnits.remove(academicUnit);
        academicUnit.setStudyField(null);
        return this;
    }

    public void setAcademicUnits(Set<AcademicUnit> academicUnits) {
        this.academicUnits = academicUnits;
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
        StudyField studyField = (StudyField) o;
        if (studyField.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), studyField.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StudyField{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            "}";
    }
}
