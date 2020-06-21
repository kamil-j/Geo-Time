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
@Table(name = "subdepartments",
    uniqueConstraints={
        @UniqueConstraint(columnNames = {"name", "department_id"}),
        @UniqueConstraint(columnNames = {"short_name", "department_id"})
    })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Subdepartment implements Serializable {

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

    @OneToMany(mappedBy = "subdepartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserExt> usersExt = new HashSet<>();

    @OneToMany(mappedBy = "subdepartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SchedulingTimeFrame> schedulingTimeFrames = new HashSet<>();

    @OneToMany(mappedBy = "subdepartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    public Subdepartment(){
    }

    public Subdepartment(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
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

    public Subdepartment name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public Subdepartment shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Department getDepartment() {
        return department;
    }

    public Subdepartment department(Department department) {
        this.department = department;
        return this;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<UserExt> getUsersExt() {
        return usersExt;
    }

    public Subdepartment usersExt(Set<UserExt> usersExt) {
        this.usersExt = usersExt;
        return this;
    }

    public Subdepartment addUserExt(UserExt userExt) {
        this.usersExt.add(userExt);
        userExt.setSubdepartment(this);
        return this;
    }

    public Subdepartment removeUserExt(UserExt userExt) {
        this.usersExt.remove(userExt);
        userExt.setSubdepartment(null);
        return this;
    }

    public void setUsersExt(Set<UserExt> usersExt) {
        this.usersExt = usersExt;
    }

    public Set<SchedulingTimeFrame> getSchedulingTimeFrames() {
        return schedulingTimeFrames;
    }

    public Subdepartment schedulingTimeFrames(Set<SchedulingTimeFrame> schedulingTimeFrames) {
        this.schedulingTimeFrames = schedulingTimeFrames;
        return this;
    }

    public Subdepartment addSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.add(schedulingTimeFrame);
        schedulingTimeFrame.setSubdepartment(this);
        return this;
    }

    public Subdepartment removeSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.remove(schedulingTimeFrame);
        schedulingTimeFrame.setSubdepartment(null);
        return this;
    }

    public void setSchedulingTimeFrames(Set<SchedulingTimeFrame> schedulingTimeFrames) {
        this.schedulingTimeFrames = schedulingTimeFrames;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public Subdepartment classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public Subdepartment addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setSubdepartment(this);
        return this;
    }

    public Subdepartment removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setSubdepartment(null);
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
        Subdepartment subdepartment = (Subdepartment) o;
        if (subdepartment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), subdepartment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Subdepartment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", shortName='" + getShortName() + "'" +
            "}";
    }
}
