package com.poulailler.intelligent.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Variable.
 */
@Entity
@Table(name = "variable")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Variable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "plage_max")
    private Long plageMax;

    @Column(name = "date_creation")
    private ZonedDateTime dateCreation;

    @Column(name = "lue")
    private Boolean lue;

    @ManyToOne
    private User consulter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Variable id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlageMax() {
        return this.plageMax;
    }

    public Variable plageMax(Long plageMax) {
        this.setPlageMax(plageMax);
        return this;
    }

    public void setPlageMax(Long plageMax) {
        this.plageMax = plageMax;
    }

    public ZonedDateTime getDateCreation() {
        return this.dateCreation;
    }

    public Variable dateCreation(ZonedDateTime dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(ZonedDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getLue() {
        return this.lue;
    }

    public Variable lue(Boolean lue) {
        this.setLue(lue);
        return this;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public User getConsulter() {
        return this.consulter;
    }

    public void setConsulter(User user) {
        this.consulter = user;
    }

    public Variable consulter(User user) {
        this.setConsulter(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variable)) {
            return false;
        }
        return id != null && id.equals(((Variable) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Variable{" +
            "id=" + getId() +
            ", plageMax=" + getPlageMax() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", lue='" + getLue() + "'" +
            "}";
    }
}
