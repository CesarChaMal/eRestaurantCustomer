package com.erestaurant.customer.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AppUser.
 */
@Table("app_user")
public class AppUser implements Serializable, Persistable<String> {

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

    @Column("description")
    private String description;

    @Column("image")
    private byte[] image;

    @Column("image_content_type")
    private String imageContentType;

    @Column("email")
    private String email;

    @Transient
    private boolean isPersisted;

    @Transient
    private User internalUser;

    @Column("internal_user_id")
    private String internalUserId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public AppUser id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AppUser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public AppUser description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public AppUser image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public AppUser imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getEmail() {
        return this.email;
    }

    public AppUser email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public AppUser setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
        this.internalUserId = user != null ? user.getId() : null;
    }

    public AppUser internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public String getInternalUserId() {
        return this.internalUserId;
    }

    public void setInternalUserId(String user) {
        this.internalUserId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUser{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
