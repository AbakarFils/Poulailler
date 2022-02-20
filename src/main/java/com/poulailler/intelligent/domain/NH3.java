package com.poulailler.intelligent.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NH3.
 */
@Entity
@Table(name = "nh3")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NH3 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "volume")
    private Long volume;

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

    public NH3 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVolume() {
        return this.volume;
    }

    public NH3 volume(Long volume) {
        this.setVolume(volume);
        return this;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public NH3 variable(Variable variable) {
        this.setVariable(variable);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NH3)) {
            return false;
        }
        return id != null && id.equals(((NH3) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NH3{" +
            "id=" + getId() +
            ", volume=" + getVolume() +
            "}";
    }
}
