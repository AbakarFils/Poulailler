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
 * Criteria class for the {@link com.poulailler.intelligent.domain.Oeuf} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.OeufResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /oeufs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OeufCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter nombreJournalier;

    private LongFilter variableId;

    private Boolean distinct;

    public OeufCriteria() {}

    public OeufCriteria(OeufCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nombreJournalier = other.nombreJournalier == null ? null : other.nombreJournalier.copy();
        this.variableId = other.variableId == null ? null : other.variableId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OeufCriteria copy() {
        return new OeufCriteria(this);
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

    public LongFilter getNombreJournalier() {
        return nombreJournalier;
    }

    public LongFilter nombreJournalier() {
        if (nombreJournalier == null) {
            nombreJournalier = new LongFilter();
        }
        return nombreJournalier;
    }

    public void setNombreJournalier(LongFilter nombreJournalier) {
        this.nombreJournalier = nombreJournalier;
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
        final OeufCriteria that = (OeufCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nombreJournalier, that.nombreJournalier) &&
            Objects.equals(variableId, that.variableId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreJournalier, variableId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OeufCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nombreJournalier != null ? "nombreJournalier=" + nombreJournalier + ", " : "") +
            (variableId != null ? "variableId=" + variableId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
