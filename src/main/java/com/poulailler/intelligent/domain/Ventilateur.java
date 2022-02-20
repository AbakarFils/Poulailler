package com.poulailler.intelligent.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ventilateur.
 */
@Entity
@Table(name = "ventilateur")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ventilateur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "vitesse")
    private Integer vitesse;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ventilateur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Ventilateur libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getVitesse() {
        return this.vitesse;
    }

    public Ventilateur vitesse(Integer vitesse) {
        this.setVitesse(vitesse);
        return this;
    }

    public void setVitesse(Integer vitesse) {
        this.vitesse = vitesse;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ventilateur)) {
            return false;
        }
        return id != null && id.equals(((Ventilateur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ventilateur{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", vitesse=" + getVitesse() +
            "}";
    }
}
