package com.springvuegradle.seng302team600.model;

import org.springframework.boot.context.properties.ConstructorBinding;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "activity_type")
@ConstructorBinding
public class ActivityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(mappedBy = "activityTypes")
    Set<Activity> referencingActivities;

    @ManyToMany(mappedBy = "activityTypes")
    Set<User> referencingUsers;


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public ActivityType(String name) {
        this.name = name;
    }

    /**
     * Default constructor mandatory for repository actions.
     */
    public ActivityType() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ActivityType) {
            final ActivityType other = (ActivityType) obj;
            return this.getName().equals(other.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.getName().replace(' ', '-'), Integer.toHexString(this.hashCode()));
    }
}