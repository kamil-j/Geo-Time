package pl.edu.agh.geotime.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "semesters")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Semester implements Serializable {

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
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SchedulingTimeFrame> schedulingTimeFrames = new HashSet<>();

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

    public Semester name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public Semester startDate(ZonedDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public Semester endDate(ZonedDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean isActive() {
        return active;
    }

    public Semester active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public Semester classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public Semester addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setSemester(this);
        return this;
    }

    public Semester removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setSemester(null);
        return this;
    }

    public void setClassUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
    }

    public Set<SchedulingTimeFrame> getSchedulingTimeFrames() {
        return schedulingTimeFrames;
    }

    public Semester schedulingTimeFrames(Set<SchedulingTimeFrame> schedulingTimeFrames) {
        this.schedulingTimeFrames = schedulingTimeFrames;
        return this;
    }

    public Semester addSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.add(schedulingTimeFrame);
        schedulingTimeFrame.setSemester(this);
        return this;
    }

    public Semester removeSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.remove(schedulingTimeFrame);
        schedulingTimeFrame.setSemester(null);
        return this;
    }

    public void setSchedulingTimeFrames(Set<SchedulingTimeFrame> schedulingTimeFrames) {
        this.schedulingTimeFrames = schedulingTimeFrames;
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
        Semester semester = (Semester) o;
        if (semester.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), semester.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Semester{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", active='" + isActive() + "'" +
            "}";
    }
}
