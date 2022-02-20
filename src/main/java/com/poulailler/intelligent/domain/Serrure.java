package com.poulailler.intelligent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Serrure.
 */
@Entity
@Table(name = "serrure")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Serrure implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "dimension")
    private Long dimension;

    @JsonIgnoreProperties(value = { "employes", "gestionnaires" }, allowSetters = true)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Equipement equipement;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Serrure id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Serrure libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getDimension() {
        return this.dimension;
    }

    public Serrure dimension(Long dimension) {
        this.setDimension(dimension);
        return this;
    }

    public void setDimension(Long dimension) {
        this.dimension = dimension;
    }

    public Equipement getEquipement() {
        return this.equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public Serrure equipement(Equipement equipement) {
        this.setEquipement(equipement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Serrure)) {
            return false;
        }
        return id != null && id.equals(((Serrure) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Serrure{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", dimension=" + getDimension() +
            "}";
    }
}
