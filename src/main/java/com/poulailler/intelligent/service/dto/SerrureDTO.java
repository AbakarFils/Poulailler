package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Serrure} entity.
 */
public class SerrureDTO implements Serializable {

    private Long id;

    private String libelle;

    private Long dimension;

    private EquipementDTO equipement;

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

    public Long getDimension() {
        return dimension;
    }

    public void setDimension(Long dimension) {
        this.dimension = dimension;
    }

    public EquipementDTO getEquipement() {
        return equipement;
    }

    public void setEquipement(EquipementDTO equipement) {
        this.equipement = equipement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SerrureDTO)) {
            return false;
        }

        SerrureDTO serrureDTO = (SerrureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, serrureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SerrureDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", dimension=" + getDimension() +
            ", equipement=" + getEquipement() +
            "}";
    }
}
