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
@Table(name = "user_groups")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Size(max = 50)
    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserExt> usersExt = new HashSet<>();

    @OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL)
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

    public UserGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public UserGroup description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<UserExt> getUsersExt() {
        return usersExt;
    }

    public UserGroup usersExt(Set<UserExt> usersExt) {
        this.usersExt = usersExt;
        return this;
    }

    public UserGroup addUserExt(UserExt userExt) {
        this.usersExt.add(userExt);
        userExt.setUserGroup(this);
        return this;
    }

    public UserGroup removeUserExt(UserExt userExt) {
        this.usersExt.remove(userExt);
        userExt.setUserGroup(null);
        return this;
    }

    public void setUsersExt(Set<UserExt> usersExt) {
        this.usersExt = usersExt;
    }

    public Set<SchedulingTimeFrame> getSchedulingTimeFrames() {
        return schedulingTimeFrames;
    }

    public UserGroup schedulingTimeFrames(Set<SchedulingTimeFrame> schedulingTimeFrames) {
        this.schedulingTimeFrames = schedulingTimeFrames;
        return this;
    }

    public UserGroup addSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.add(schedulingTimeFrame);
        schedulingTimeFrame.setUserGroup(this);
        return this;
    }

    public UserGroup removeSchedulingTimeFrame(SchedulingTimeFrame schedulingTimeFrame) {
        this.schedulingTimeFrames.remove(schedulingTimeFrame);
        schedulingTimeFrame.setUserGroup(null);
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
        UserGroup userGroup = (UserGroup) o;
        if (userGroup.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userGroup.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
