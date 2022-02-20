package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Ventilateur} entity.
 */
public class VentilateurDTO implements Serializable {

    private Long id;

    private String libelle;

    private Integer vitesse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getVitesse() {
        return vitesse;
    }

    public void setVitesse(Integer vitesse) {
        this.vitesse = vitesse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentilateurDTO)) {
            return false;
        }

        VentilateurDTO ventilateurDTO = (VentilateurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ventilateurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentilateurDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", vitesse=" + getVitesse() +
            "}";
    }
}
