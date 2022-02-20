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
 * Criteria class for the {@link com.poulailler.intelligent.domain.Humidite} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.HumiditeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /humidites?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class HumiditeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter niveau;

    private LongFilter variableId;

    private Boolean distinct;

    public HumiditeCriteria() {}

    public HumiditeCriteria(HumiditeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.niveau = other.niveau == null ? null : other.niveau.copy();
        this.variableId = other.variableId == null ? null : other.variableId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public HumiditeCriteria copy() {
        return new HumiditeCriteria(this);
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

    public LongFilter getNiveau() {
        return niveau;
    }

    public LongFilter niveau() {
        if (niveau == null) {
            niveau = new LongFilter();
        }
        return niveau;
    }

    public void setNiveau(LongFilter niveau) {
        this.niveau = niveau;
    }

    public LongFilter getVariableId() {
        return variableId;
    }

    public LongFilter variableId() {
        if (variableId == null) {
            variableId = new LongFilter();
        }
        return variableId;
    }

    public void setVariableId(LongFilter variableId) {
        this.variableId = variableId;
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
        final HumiditeCriteria that = (HumiditeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(niveau, that.niveau) &&
            Objects.equals(variableId, that.variableId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, niveau, variableId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HumiditeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (niveau != null ? "niveau=" + niveau + ", " : "") +
            (variableId != null ? "variableId=" + variableId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
