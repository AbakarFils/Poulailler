package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Directeur} entity.
 */
public class DirecteurDTO implements Serializable {

    private Long id;

    private String adresse;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DirecteurDTO)) {
            return false;
        }

        DirecteurDTO directeurDTO = (DirecteurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, directeurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DirecteurDTO{" +
            "id=" + getId() +
            ", adresse='" + getAdresse() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
