package com.erestaurant.customer.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.erestaurant.customer.domain.Products} entity.
 */
public class ProductsDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    private String name;

    @Lob
    private String description;

    @Lob
    private byte[] image;

    private String imageContentType;

    @NotNull(message = "must not be null")
    private Float estimatedPreparaingTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Float getEstimatedPreparaingTime() {
        return estimatedPreparaingTime;
    }

    public void setEstimatedPreparaingTime(Float estimatedPreparaingTime) {
        this.estimatedPreparaingTime = estimatedPreparaingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductsDTO)) {
            return false;
        }

        ProductsDTO productsDTO = (ProductsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", estimatedPreparaingTime=" + getEstimatedPreparaingTime() +
            "}";
    }
}
