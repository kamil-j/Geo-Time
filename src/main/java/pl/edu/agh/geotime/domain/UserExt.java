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
@Table(name = "users_ext")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserExt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne(optional = false)
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    private Subdepartment subdepartment;

    @OneToMany(mappedBy = "userExt", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ClassUnit> classUnits = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private UserGroup userGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subdepartment getSubdepartment() {
        return subdepartment;
    }

    public UserExt subdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
        return this;
    }

    public void setSubdepartment(Subdepartment subdepartment) {
        this.subdepartment = subdepartment;
    }

    public Set<ClassUnit> getClassUnits() {
        return classUnits;
    }

    public UserExt classUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
        return this;
    }

    public UserExt addClassUnit(ClassUnit classUnit) {
        this.classUnits.add(classUnit);
        classUnit.setUserExt(this);
        return this;
    }

    public UserExt removeClassUnit(ClassUnit classUnit) {
        this.classUnits.remove(classUnit);
        classUnit.setUserExt(null);
        return this;
    }

    public void setClassUnits(Set<ClassUnit> classUnits) {
        this.classUnits = classUnits;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public UserExt userGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
        return this;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
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
        UserExt userExt = (UserExt) o;
        if (userExt.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userExt.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserExt{" +
            "id=" + getId() +
            "user=" + getUser() +
            "userGroup=" + getUserGroup() +
            "}";
    }
}
