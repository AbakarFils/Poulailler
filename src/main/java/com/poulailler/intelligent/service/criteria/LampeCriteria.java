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
 * Criteria class for the {@link com.poulailler.intelligent.domain.Lampe} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.LampeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lampes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LampeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter libelle;

    private LongFilter equipementId;

    private Boolean distinct;

    public LampeCriteria() {}

    public LampeCriteria(LampeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.libelle = other.libelle == null ? null : other.libelle.copy();
        this.equipementId = other.equipementId == null ? null : other.equipementId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LampeCriteria copy() {
        return new LampeCriteria(this);
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

    public StringFilter getLibelle() {
        return libelle;
    }

    public StringFilter libelle() {
        if (libelle == null) {
            libelle = new StringFilter();
        }
        return libelle;
    }

    public void setLibelle(StringFilter libelle) {
        this.libelle = libelle;
    }

    public LongFilter getEquipementId() {
        return equipementId;
    }

    public LongFilter equipementId() {
        if (equipementId == null) {
            equipementId = new LongFilter();
        }
        return equipementId;
    }

    public void setEquipementId(LongFilter equipementId) {
        this.equipementId = equipementId;
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
        final LampeCriteria that = (LampeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(equipementId, that.equipementId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, equipementId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LampeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (libelle != null ? "libelle=" + libelle + ", " : "") +
            (equipementId != null ? "equipementId=" + equipementId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
