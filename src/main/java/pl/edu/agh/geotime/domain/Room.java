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
@Table(name = "rooms", uniqueConstraints={
    @UniqueConstraint(columnNames = {"name", "location_id"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Min(value = 0)
    @Column(name = "capacity")
    private Integer capacity;

    @ManyToOne(optional = false)
    @NotNull
    private RoomType roomType;

    @ManyToOne(optional = false)
    @NotNull
    private Location location;

    @ManyToMany(mappedBy = "rooms")
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BookingUnit> bookingUnits = new HashSet<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ScheduleUnit> scheduleUnits = new HashSet<>();

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

    public Room name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Room capacity(Integer capacity) {
        this.capacity = capacity;
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public Room roomType(RoomType roomType) {
        this.roomType = roomType;
        return this;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Location getLocation() {
        return location;
    }

    public Room location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public Room classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public Room addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.getRooms().add(this);
        return this;
    }

    public Room removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.getRooms().remove(this);
        return this;
    }

    public void setClassUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
    }

    public Set<BookingUnit> getBookingUnits() {
        return bookingUnits;
    }

    public Room bookingUnits(Set<BookingUnit> bookingUnits) {
        this.bookingUnits = bookingUnits;
        return this;
    }

    public Room addBookingUnit(BookingUnit bookingUnit) {
        this.bookingUnits.add(bookingUnit);
        bookingUnit.setRoom(this);
        return this;
    }

    public Room removeBookingUnit(BookingUnit bookingUnit) {
        this.bookingUnits.remove(bookingUnit);
        bookingUnit.setRoom(null);
        return this;
    }

    public void setBookingUnits(Set<BookingUnit> bookingUnits) {
        this.bookingUnits = bookingUnits;
    }

    public Set<ScheduleUnit> getScheduleUnits() {
        return scheduleUnits;
    }

    public Room scheduleUnits(Set<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
        return this;
    }

    public Room addScheduleUnit(ScheduleUnit scheduleUnit) {
        this.scheduleUnits.add(scheduleUnit);
        scheduleUnit.setRoom(this);
        return this;
    }

    public Room removeScheduleUnit(ScheduleUnit scheduleUnit) {
        this.scheduleUnits.remove(scheduleUnit);
        scheduleUnit.setRoom(null);
        return this;
    }

    public void setScheduleUnits(Set<ScheduleUnit> scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
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
        Room room = (Room) o;
        if (room.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), room.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", capacity=" + getCapacity() +
            "}";
    }
}
