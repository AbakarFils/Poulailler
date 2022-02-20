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
 * Criteria class for the {@link com.poulailler.intelligent.domain.Temperature} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.TemperatureResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /temperatures?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TemperatureCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter dregree;

    private LongFilter variableId;

    private Boolean distinct;

    public TemperatureCriteria() {}

    public TemperatureCriteria(TemperatureCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.dregree = other.dregree == null ? null : other.dregree.copy();
        this.variableId = other.variableId == null ? null : other.variableId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TemperatureCriteria copy() {
        return new TemperatureCriteria(this);
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

    public LongFilter getDregree() {
        return dregree;
    }

    public LongFilter dregree() {
        if (dregree == null) {
            dregree = new LongFilter();
        }
        return dregree;
    }

    public void setDregree(LongFilter dregree) {
        this.dregree = dregree;
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
        final TemperatureCriteria that = (TemperatureCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(dregree, that.dregree) &&
            Objects.equals(variableId, that.variableId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dregree, variableId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemperatureCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (dregree != null ? "dregree=" + dregree + ", " : "") +
            (variableId != null ? "variableId=" + variableId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
