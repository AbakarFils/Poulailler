package com.poulailler.intelligent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Temperature.
 */
@Entity
@Table(name = "temperature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Temperature implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "dregree")
    private Long dregree;

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

    public Temperature id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDregree() {
        return this.dregree;
    }

    public Temperature dregree(Long dregree) {
        this.setDregree(dregree);
        return this;
    }

    public void setDregree(Long dregree) {
        this.dregree = dregree;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public Temperature variable(Variable variable) {
        this.setVariable(variable);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Temperature)) {
            return false;
        }
        return id != null && id.equals(((Temperature) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Temperature{" +
            "id=" + getId() +
            ", dregree=" + getDregree() +
            "}";
    }
}
