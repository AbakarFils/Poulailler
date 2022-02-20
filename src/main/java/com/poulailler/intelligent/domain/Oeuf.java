package com.poulailler.intelligent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Oeuf.
 */
@Entity
@Table(name = "oeuf")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Oeuf implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre_journalier")
    private Long nombreJournalier;

    @JsonIgnoreProperties(value = { "consulter" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private Variable variable;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Oeuf id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNombreJournalier() {
        return this.nombreJournalier;
    }

    public Oeuf nombreJournalier(Long nombreJournalier) {
        this.setNombreJournalier(nombreJournalier);
        return this;
    }

    public void setNombreJournalier(Long nombreJournalier) {
        this.nombreJournalier = nombreJournalier;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public Oeuf variable(Variable variable) {
        this.setVariable(variable);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Oeuf)) {
            return false;
        }
        return id != null && id.equals(((Oeuf) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Oeuf{" +
            "id=" + getId() +
            ", nombreJournalier=" + getNombreJournalier() +
            "}";
    }
}
