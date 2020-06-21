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
@Table(name = "class_types")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClassType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @NotNull
    @Size(max = 10)
    @Column(name = "short_name", length = 10, nullable = false, unique = true)
    private String shortName;

    @Size(max = 50)
    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "color")
    private String color;

    @OneToMany(mappedBy = "classType", cascade = CascadeType.ALL)
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

    public ClassType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public ClassType shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public ClassType description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public ClassType color(String color) {
        this.color = color;
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public ClassType classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public ClassType addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setClassType(this);
        return this;
    }

    public ClassType removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setClassType(null);
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
        ClassType classType = (ClassType) o;
        if (classType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), classType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClassType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", description='" + getDescription() + "'" +
            ", color='" + getColor() + "'" +
            "}";
    }
}
