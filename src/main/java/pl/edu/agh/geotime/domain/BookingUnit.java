package pl.edu.agh.geotime.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import pl.edu.agh.geotime.domain.enumeration.DayOfWeek;

import pl.edu.agh.geotime.domain.enumeration.WeekType;

import pl.edu.agh.geotime.domain.enumeration.SemesterHalf;

@Entity
@Table(name = "booking_units")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BookingUnit implements Serializable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private DayOfWeek day;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "week", nullable = false)
    private WeekType week;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester_half")
    private SemesterHalf semesterHalf;

    @Column(name = "locked")
    private Boolean locked;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private ClassUnit classUnit;

    @ManyToOne(optional = false)
    @NotNull
    private Room room;

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

    public BookingUnit startTime(ZonedDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public BookingUnit endTime(ZonedDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public BookingUnit day(DayOfWeek day) {
        this.day = day;
        return this;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public WeekType getWeek() {
        return week;
    }

    public BookingUnit week(WeekType week) {
        this.week = week;
        return this;
    }

    public void setWeek(WeekType week) {
        this.week = week;
    }

    public SemesterHalf getSemesterHalf() {
        return semesterHalf;
    }

    public BookingUnit semesterHalf(SemesterHalf semesterHalf) {
        this.semesterHalf = semesterHalf;
        return this;
    }

    public void setSemesterHalf(SemesterHalf semesterHalf) {
        this.semesterHalf = semesterHalf;
    }

    public Boolean isLocked() {
        return locked;
    }

    public BookingUnit locked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public ClassUnit getClassUnit() {
        return classUnit;
    }

    public BookingUnit classUnit(ClassUnit classUnit) {
        this.classUnit = classUnit;
        return this;
    }

    public void setClassUnit(ClassUnit classUnit) {
        this.classUnit = classUnit;
    }

    public Room getRoom() {
        return room;
    }

    public BookingUnit room(Room room) {
        this.room = room;
        return this;
    }

    public void setRoom(Room room) {
        this.room = room;
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
        BookingUnit bookingUnit = (BookingUnit) o;
        if (bookingUnit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), bookingUnit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BookingUnit{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", day='" + getDay() + "'" +
            ", week='" + getWeek() + "'" +
            ", semesterHalf='" + getSemesterHalf() + "'" +
            ", locked='" + isLocked() + "'" +
            "}";
    }
}
