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
@Table(name = "class_unit_groups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClassUnitGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "classUnitGroup", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Department department;

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

    public ClassUnitGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public ClassUnitGroup description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public ClassUnitGroup classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public ClassUnitGroup addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setClassUnitGroup(this);
        return this;
    }

    public ClassUnitGroup removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setClassUnitGroup(null);
        return this;
    }

    public void setClassUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
    }

    public Department getDepartment() {
        return department;
    }

    public ClassUnitGroup department(Department department) {
        this.department = department;
        return this;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
        ClassUnitGroup classUnitGroup = (ClassUnitGroup) o;
        if (classUnitGroup.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), classUnitGroup.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClassUnitGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
