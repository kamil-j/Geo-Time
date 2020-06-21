package pl.edu.agh.geotime.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "scheduling_time_frames",
    uniqueConstraints={@UniqueConstraint(columnNames = {"user_group_id", "subdepartment_id", "semester_id"})}
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SchedulingTimeFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    @ManyToOne(optional = false)
    @NotNull
    private UserGroup userGroup;

    @ManyToOne(optional = false)
    @NotNull
    private Subdepartment subdepartment;

    @ManyToOne(optional = false)
    @NotNull
    private Semester semester;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public SchedulingTimeFrame startTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public SchedulingTimeFrame endTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public SchedulingTimeFrame userGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
        return this;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Subdepartment getSubdepartment() {
        return subdepartment;
    }

    public SchedulingTimeFrame subdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
        return this;
    }

    public void setSubdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
    }

    public Semester getSemester() {
        return semester;
    }

    public SchedulingTimeFrame semester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
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
        SchedulingTimeFrame schedulingTimeFrame = (SchedulingTimeFrame) o;
        if (schedulingTimeFrame.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), schedulingTimeFrame.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SchedulingTimeFrame{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
