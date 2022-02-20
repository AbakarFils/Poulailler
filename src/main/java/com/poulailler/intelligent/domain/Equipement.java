package com.poulailler.intelligent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poulailler.intelligent.domain.enumeration.TypeEquipement;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Equipement.
 */
@Entity
@Table(name = "equipement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Equipement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "statut")
    private Boolean statut;

    @NotNull
    @Column(name = "ref_arduino", nullable = false, unique = true)
    private String refArduino;

    @Column(name = "date_crea")
    private ZonedDateTime dateCrea;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "marque")
    private String marque;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeEquipement type;

    @ManyToMany
    @JoinTable(
        name = "rel_equipement__employe",
        joinColumns = @JoinColumn(name = "equipement_id"),
        inverseJoinColumns = @JoinColumn(name = "employe_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<Employe> employes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_equipement__gestionnaire",
        joinColumns = @JoinColumn(name = "equipement_id"),
        inverseJoinColumns = @JoinColumn(name = "gestionnaire_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<Directeur> gestionnaires = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatut() {
        return this.statut;
    }

    public Equipement statut(Boolean statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(Boolean statut) {
        this.statut = statut;
    }

    public String getRefArduino() {
        return this.refArduino;
    }

    public Equipement refArduino(String refArduino) {
        this.setRefArduino(refArduino);
        return this;
    }

    public void setRefArduino(String refArduino) {
        this.refArduino = refArduino;
    }

    public ZonedDateTime getDateCrea() {
        return this.dateCrea;
    }

    public Equipement dateCrea(ZonedDateTime dateCrea) {
        this.setDateCrea(dateCrea);
        return this;
    }

    public void setDateCrea(ZonedDateTime dateCrea) {
        this.dateCrea = dateCrea;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Equipement libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getMarque() {
        return this.marque;
    }

    public Equipement marque(String marque) {
        this.setMarque(marque);
        return this;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public TypeEquipement getType() {
        return this.type;
    }

    public Equipement type(TypeEquipement type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeEquipement type) {
        this.type = type;
    }

    public Set<Employe> getEmployes() {
        return this.employes;
    }

    public void setEmployes(Set<Employe> employes) {
        this.employes = employes;
    }

    public Equipement employes(Set<Employe> employes) {
        this.setEmployes(employes);
        return this;
    }

    public Equipement addEmploye(Employe employe) {
        this.employes.add(employe);
        return this;
    }

    public Equipement removeEmploye(Employe employe) {
        this.employes.remove(employe);
        return this;
    }

    public Set<Directeur> getGestionnaires() {
        return this.gestionnaires;
    }

    public void setGestionnaires(Set<Directeur> directeurs) {
        this.gestionnaires = directeurs;
    }

    public Equipement gestionnaires(Set<Directeur> directeurs) {
        this.setGestionnaires(directeurs);
        return this;
    }

    public Equipement addGestionnaire(Directeur directeur) {
        this.gestionnaires.add(directeur);
        return this;
    }

    public Equipement removeGestionnaire(Directeur directeur) {
        this.gestionnaires.remove(directeur);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipement)) {
            return false;
        }
        return id != null && id.equals(((Equipement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipement{" +
            "id=" + getId() +
            ", statut='" + getStatut() + "'" +
            ", refArduino='" + getRefArduino() + "'" +
            ", dateCrea='" + getDateCrea() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", marque='" + getMarque() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
