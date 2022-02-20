package com.poulailler.intelligent.service.dto;

import com.poulailler.intelligent.domain.enumeration.TypeEquipement;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Equipement} entity.
 */
public class EquipementDTO implements Serializable {

    private Long id;

    private Boolean statut;

    @NotNull
    private String refArduino;

    private ZonedDateTime dateCrea;

    @NotNull
    private String libelle;

    private String marque;

    private TypeEquipement type;

    private Set<EmployeDTO> employes = new HashSet<>();

    private Set<DirecteurDTO> gestionnaires = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatut() {
        return statut;
    }

    public void setStatut(Boolean statut) {
        this.statut = statut;
    }

    public String getRefArduino() {
        return refArduino;
    }

    public void setRefArduino(String refArduino) {
        this.refArduino = refArduino;
    }

    public ZonedDateTime getDateCrea() {
        return dateCrea;
    }

    public void setDateCrea(ZonedDateTime dateCrea) {
        this.dateCrea = dateCrea;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public TypeEquipement getType() {
        return type;
    }

    public void setType(TypeEquipement type) {
        this.type = type;
    }

    public Set<EmployeDTO> getEmployes() {
        return employes;
    }

    public void setEmployes(Set<EmployeDTO> employes) {
        this.employes = employes;
    }

    public Set<DirecteurDTO> getGestionnaires() {
        return gestionnaires;
    }

    public void setGestionnaires(Set<DirecteurDTO> gestionnaires) {
        this.gestionnaires = gestionnaires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipementDTO)) {
            return false;
        }

        EquipementDTO equipementDTO = (EquipementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipementDTO{" +
            "id=" + getId() +
            ", statut='" + getStatut() + "'" +
            ", refArduino='" + getRefArduino() + "'" +
            ", dateCrea='" + getDateCrea() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", marque='" + getMarque() + "'" +
            ", type='" + getType() + "'" +
            ", employes=" + getEmployes() +
            ", gestionnaires=" + getGestionnaires() +
            "}";
    }
}
