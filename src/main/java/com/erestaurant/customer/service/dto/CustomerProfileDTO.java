package com.erestaurant.customer.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.erestaurant.customer.domain.CustomerProfile} entity.
 */
public class CustomerProfileDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    private String name;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    private String location;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    private String locationRange;

    @Lob
    private String referals;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationRange() {
        return locationRange;
    }

    public void setLocationRange(String locationRange) {
        this.locationRange = locationRange;
    }

    public String getReferals() {
        return referals;
    }

    public void setReferals(String referals) {
        this.referals = referals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerProfileDTO)) {
            return false;
        }

        CustomerProfileDTO customerProfileDTO = (CustomerProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerProfileDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            ", locationRange='" + getLocationRange() + "'" +
            ", referals='" + getReferals() + "'" +
            "}";
    }
}
