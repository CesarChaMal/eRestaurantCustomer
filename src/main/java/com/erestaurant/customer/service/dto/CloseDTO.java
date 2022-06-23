package com.erestaurant.customer.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.erestaurant.customer.domain.Close} entity.
 */
public class CloseDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String id;

    @Lob
    private String description;

    private Boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloseDTO)) {
            return false;
        }

        CloseDTO closeDTO = (CloseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, closeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CloseDTO{" +
            "id='" + getId() + "'" +
            ", description='" + getDescription() + "'" +
            ", enabled='" + getEnabled() + "'" +
            "}";
    }
}
