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

import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;

import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

@Entity
@Table(name = "class_units")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClassUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "title", length = 80, nullable = false)
    private String title;

    @Size(max = 100)
    @Column(name = "description", length = 100)
    private String description;

    @NotNull
    @Min(value = 0)
    @Max(value = 525600)
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Min(value = 0)
    @Max(value = 1000)
    @Column(name = "hours_quantity", nullable = false)
    private Integer hoursQuantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private ClassFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_unit_group")
    private AcademicUnitGroup academicUnitGroup;

    @NotNull
    @Column(name = "only_semester_half", nullable = false)
    private Boolean onlySemesterHalf;

    @ManyToOne(optional = false)
    @NotNull
    private ClassType classType;

    @ManyToOne
    private UserExt userExt;

    @ManyToMany
    @NotNull
    @JoinTable(name = "class_units_rooms",
               joinColumns = @JoinColumn(name="class_unit_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="room_id", referencedColumnName="id"))
    private Set<Room> rooms = new HashSet<>();

    @OneToOne(mappedBy = "classUnit")
    @JsonIgnore
    private BookingUnit bookingUnit;

    @OneToMany(mappedBy = "classUnit", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ScheduleUnit> scheduleUnits = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Semester semester;

    @ManyToOne(optional = false)
    @NotNull
    private AcademicUnit academicUnit;

    @ManyToOne
    private ClassUnitGroup classUnitGroup;

    @ManyToOne(optional = false)
    @NotNull
    private Subdepartment subdepartment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public ClassUnit title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public ClassUnit description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public ClassUnit duration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getHoursQuantity() {
        return hoursQuantity;
    }

    public ClassUnit hoursQuantity(Integer hoursQuantity) {
        this.hoursQuantity = hoursQuantity;
        return this;
    }

    public void setHoursQuantity(Integer hoursQuantity) {
        this.hoursQuantity = hoursQuantity;
    }

    public ClassFrequency getFrequency() {
        return frequency;
    }

    public ClassUnit frequency(ClassFrequency frequency) {
        this.frequency = frequency;
        return this;
    }

    public void setFrequency(ClassFrequency frequency) {
        this.frequency = frequency;
    }

    public AcademicUnitGroup getAcademicUnitGroup() {
        return academicUnitGroup;
    }

    public ClassUnit academicUnitGroup(AcademicUnitGroup academicUnitGroup) {
        this.academicUnitGroup = academicUnitGroup;
        return this;
    }

    public void setAcademicUnitGroup(AcademicUnitGroup academicUnitGroup) {
        this.academicUnitGroup = academicUnitGroup;
    }

    public Boolean isOnlySemesterHalf() {
        return onlySemesterHalf;
    }

    public ClassUnit onlySemesterHalf(Boolean onlySemesterHalf) {
        this.onlySemesterHalf = onlySemesterHalf;
        return this;
    }

    public void setOnlySemesterHalf(Boolean onlySemesterHalf) {
        this.onlySemesterHalf = onlySemesterHalf;
    }

    public ClassType getClassType() {
        return classType;
    }

    public ClassUnit classType(ClassType classType) {
        this.classType = classType;
        return this;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public UserExt getUserExt() {
        return userExt;
    }

    public ClassUnit userExt(UserExt userExt) {
        this.userExt = userExt;
        return this;
    }

    public void setUserExt(UserExt userExt) {
        this.userExt = userExt;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public ClassUnit rooms(Set<Room> rooms) {
        this.rooms = rooms;
        return this;
    }

    public ClassUnit addRoom(Room room) {
        this.rooms.add(room);
        room.getClassUnits().add(this);
        return this;
    }

    public ClassUnit removeRoom(Room room) {
        this.rooms.remove(room);
        room.getClassUnits().remove(this);
        return this;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public BookingUnit getBookingUnit() {
        return bookingUnit;
    }

    public ClassUnit bookingUnit(BookingUnit bookingUnit) {
        this.bookingUnit = bookingUnit;
        return this;
    }

    public void setBookingUnit(BookingUnit bookingUnit) {
        this.bookingUnit = bookingUnit;
    }

    public Set<ScheduleUnit> getScheduleUnits() {
        return scheduleUnits;
    }

    public ClassUnit scheduleUnits(Set<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
        return this;
    }

    public ClassUnit addScheduleUnit(ScheduleUnit scheduleUnit) {
        this.scheduleUnits.add(scheduleUnit);
        scheduleUnit.setClassUnit(this);
        return this;
    }

    public ClassUnit removeScheduleUnit(ScheduleUnit scheduleUnit) {
        this.scheduleUnits.remove(scheduleUnit);
        scheduleUnit.setClassUnit(null);
        return this;
    }

    public void setScheduleUnits(Set<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
    }

    public Semester getSemester() {
        return semester;
    }

    public ClassUnit semester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public AcademicUnit getAcademicUnit() {
        return academicUnit;
    }

    public ClassUnit academicUnit(AcademicUnit academicUnit) {
        this.academicUnit = academicUnit;
        return this;
    }

    public void setAcademicUnit(AcademicUnit academicUnit) {
        this.academicUnit = academicUnit;
    }

    public ClassUnitGroup getClassUnitGroup() {
        return classUnitGroup;
    }

    public ClassUnit classUnitGroup(ClassUnitGroup classUnitGroup) {
        this.classUnitGroup = classUnitGroup;
        return this;
    }

    public void setClassUnitGroup(ClassUnitGroup classUnitGroup) {
        this.classUnitGroup = classUnitGroup;
    }

    public Subdepartment getSubdepartment() {
        return subdepartment;
    }

    public ClassUnit subdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
        return this;
    }

    public void setSubdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
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
        ClassUnit classUnit = (ClassUnit) o;
        if (classUnit.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), classUnit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClassUnit{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", duration=" + getDuration() +
            ", hoursQuantity=" + getHoursQuantity() +
            ", frequency='" + getFrequency() + "'" +
            ", academicUnitGroup='" + getAcademicUnitGroup() + "'" +
            ", onlySemesterHalf='" + isOnlySemesterHalf() + "'" +
            "}";
    }
}
