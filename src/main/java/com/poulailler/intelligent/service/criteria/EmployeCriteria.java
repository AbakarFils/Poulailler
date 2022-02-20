package com.poulailler.intelligent.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.poulailler.intelligent.domain.Employe} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.EmployeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EmployeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numeroIdentite;

    private StringFilter adresse;

    private LongFilter userId;

    private Boolean distinct;

    public EmployeCriteria() {}

    public EmployeCriteria(EmployeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.numeroIdentite = other.numeroIdentite == null ? null : other.numeroIdentite.copy();
        this.adresse = other.adresse == null ? null : other.adresse.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EmployeCriteria copy() {
        return new EmployeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNumeroIdentite() {
        return numeroIdentite;
    }

    public StringFilter numeroIdentite() {
        if (numeroIdentite == null) {
            numeroIdentite = new StringFilter();
        }
        return numeroIdentite;
    }

    public void setNumeroIdentite(StringFilter numeroIdentite) {
        this.numeroIdentite = numeroIdentite;
    }

    public StringFilter getAdresse() {
        return adresse;
    }

    public StringFilter adresse() {
        if (adresse == null) {
            adresse = new StringFilter();
        }
        return adresse;
    }

    public void setAdresse(StringFilter adresse) {
        this.adresse = adresse;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeCriteria that = (EmployeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numeroIdentite, that.numeroIdentite) &&
            Objects.equals(adresse, that.adresse) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroIdentite, adresse, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (numeroIdentite != null ? "numeroIdentite=" + numeroIdentite + ", " : "") +
            (adresse != null ? "adresse=" + adresse + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
