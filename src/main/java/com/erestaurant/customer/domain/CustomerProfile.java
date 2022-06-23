package com.erestaurant.customer.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CustomerProfile.
 */
@Table("customer_profile")
public class CustomerProfile implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Id
    @Column("id")
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("location")
    private String location;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("location_range")
    private String locationRange;

    @Column("referals")
    private String referals;

    @Transient
    private boolean isPersisted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public CustomerProfile id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CustomerProfile name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public CustomerProfile location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationRange() {
        return this.locationRange;
    }

    public CustomerProfile locationRange(String locationRange) {
        this.setLocationRange(locationRange);
        return this;
    }

    public void setLocationRange(String locationRange) {
        this.locationRange = locationRange;
    }

    public String getReferals() {
        return this.referals;
    }

    public CustomerProfile referals(String referals) {
        this.setReferals(referals);
        return this;
    }

    public void setReferals(String referals) {
        this.referals = referals;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public CustomerProfile setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerProfile)) {
            return false;
        }
        return id != null && id.equals(((CustomerProfile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerProfile{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            ", locationRange='" + getLocationRange() + "'" +
            ", referals='" + getReferals() + "'" +
            "}";
    }
}
