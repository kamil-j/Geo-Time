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
@Table(name = "departments")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department implements Serializable {

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

    @Column(name = "functional")
    private Boolean functional;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Subdepartment> subdepartments = new HashSet<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<StudyField> studyFields = new HashSet<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Location> locations = new HashSet<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnitGroup> classUnitGroups = new HashSet<>();

    public Department(){
    }

    public Department(String name, String shortName, Boolean functional) {
        this.name = name;
        this.shortName = shortName;
        this.functional = functional;
    }

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

    public Department name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public Department shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean isFunctional() {
        return functional;
    }

    public Department functional(Boolean functional) {
        this.functional = functional;
        return this;
    }

    public void setFunctional(Boolean functional) {
        this.functional = functional;
    }

    public Set<Subdepartment> getSubdepartments() {
        return subdepartments;
    }

    public Department subdepartments(Set<Subdepartment> subdepartments) {
        this.subdepartments = subdepartments;
        return this;
    }

    public Department addSubdepartment(Subdepartment subdepartment) {
        this.subdepartments.add(subdepartment);
        subdepartment.setDepartment(this);
        return this;
    }

    public Department removeSubdepartment(Subdepartment subdepartment) {
        this.subdepartments.remove(subdepartment);
        subdepartment.setDepartment(null);
        return this;
    }

    public void setSubdepartments(Set<Subdepartment> subdepartments) {
        this.subdepartments = subdepartments;
    }

    public Set<StudyField> getStudyFields() {
        return studyFields;
    }

    public Department studyFields(Set<StudyField> studyFields) {
        this.studyFields = studyFields;
        return this;
    }

    public Department addStudyField(StudyField studyField) {
        this.studyFields.add(studyField);
        studyField.setDepartment(this);
        return this;
    }

    public Department removeStudyField(StudyField studyField) {
        this.studyFields.remove(studyField);
        studyField.setDepartment(null);
        return this;
    }

    public void setStudyFields(Set<StudyField> studyFields) {
        this.studyFields = studyFields;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public Department locations(Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Department addLocation(Location location) {
        this.locations.add(location);
        location.setDepartment(this);
        return this;
    }

    public Department removeLocation(Location location) {
        this.locations.remove(location);
        location.setDepartment(null);
        return this;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public Set<ClassUnitGroup> getClassUnitGroups() {
        return classUnitGroups;
    }

    public Department classUnitGroups(Set<ClassUnitGroup> classUnitGroups) {
        this.classUnitGroups = classUnitGroups;
        return this;
    }

    public Department addClassUnitGroup(ClassUnitGroup classUnitGroup) {
        this.classUnitGroups.add(classUnitGroup);
        classUnitGroup.setDepartment(this);
        return this;
    }

    public Department removeClassUnitGroup(ClassUnitGroup classUnitGroup) {
        this.classUnitGroups.remove(classUnitGroup);
        classUnitGroup.setDepartment(null);
        return this;
    }

    public void setClassUnitGroups(Set<ClassUnitGroup> classUnitGroups) {
        this.classUnitGroups = classUnitGroups;
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
        Department department = (Department) o;
        if (department.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), department.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Department{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", functional='" + isFunctional() + "'" +
            "}";
    }
}
