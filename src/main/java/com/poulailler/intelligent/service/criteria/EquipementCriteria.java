package com.poulailler.intelligent.service.criteria;

import com.poulailler.intelligent.domain.enumeration.TypeEquipement;
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
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.poulailler.intelligent.domain.Equipement} entity. This class is used
 * in {@link com.poulailler.intelligent.web.rest.EquipementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /equipements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EquipementCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeEquipement
     */
    public static class TypeEquipementFilter extends Filter<TypeEquipement> {

        public TypeEquipementFilter() {}

        public TypeEquipementFilter(TypeEquipementFilter filter) {
            super(filter);
        }

        @Override
        public TypeEquipementFilter copy() {
            return new TypeEquipementFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter statut;

    private StringFilter refArduino;

    private ZonedDateTimeFilter dateCrea;

    private StringFilter libelle;

    private StringFilter marque;

    private TypeEquipementFilter type;

    private LongFilter employeId;

    private LongFilter gestionnaireId;

    private Boolean distinct;

    public EquipementCriteria() {}

    public EquipementCriteria(EquipementCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.statut = other.statut == null ? null : other.statut.copy();
        this.refArduino = other.refArduino == null ? null : other.refArduino.copy();
        this.dateCrea = other.dateCrea == null ? null : other.dateCrea.copy();
        this.libelle = other.libelle == null ? null : other.libelle.copy();
        this.marque = other.marque == null ? null : other.marque.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.employeId = other.employeId == null ? null : other.employeId.copy();
        this.gestionnaireId = other.gestionnaireId == null ? null : other.gestionnaireId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EquipementCriteria copy() {
        return new EquipementCriteria(this);
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

    public BooleanFilter getStatut() {
        return statut;
    }

    public BooleanFilter statut() {
        if (statut == null) {
            statut = new BooleanFilter();
        }
        return statut;
    }

    public void setStatut(BooleanFilter statut) {
        this.statut = statut;
    }

    public StringFilter getRefArduino() {
        return refArduino;
    }

    public StringFilter refArduino() {
        if (refArduino == null) {
            refArduino = new StringFilter();
        }
        return refArduino;
    }

    public void setRefArduino(StringFilter refArduino) {
        this.refArduino = refArduino;
    }

    public ZonedDateTimeFilter getDateCrea() {
        return dateCrea;
    }

    public ZonedDateTimeFilter dateCrea() {
        if (dateCrea == null) {
            dateCrea = new ZonedDateTimeFilter();
        }
        return dateCrea;
    }

    public void setDateCrea(ZonedDateTimeFilter dateCrea) {
        this.dateCrea = dateCrea;
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

    public StringFilter getMarque() {
        return marque;
    }

    public StringFilter marque() {
        if (marque == null) {
            marque = new StringFilter();
        }
        return marque;
    }

    public void setMarque(StringFilter marque) {
        this.marque = marque;
    }

    public TypeEquipementFilter getType() {
        return type;
    }

    public TypeEquipementFilter type() {
        if (type == null) {
            type = new TypeEquipementFilter();
        }
        return type;
    }

    public void setType(TypeEquipementFilter type) {
        this.type = type;
    }

    public LongFilter getEmployeId() {
        return employeId;
    }

    public LongFilter employeId() {
        if (employeId == null) {
            employeId = new LongFilter();
        }
        return employeId;
    }

    public void setEmployeId(LongFilter employeId) {
        this.employeId = employeId;
    }

    public LongFilter getGestionnaireId() {
        return gestionnaireId;
    }

    public LongFilter gestionnaireId() {
        if (gestionnaireId == null) {
            gestionnaireId = new LongFilter();
        }
        return gestionnaireId;
    }

    public void setGestionnaireId(LongFilter gestionnaireId) {
        this.gestionnaireId = gestionnaireId;
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
        final EquipementCriteria that = (EquipementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(refArduino, that.refArduino) &&
            Objects.equals(dateCrea, that.dateCrea) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(marque, that.marque) &&
            Objects.equals(type, that.type) &&
            Objects.equals(employeId, that.employeId) &&
            Objects.equals(gestionnaireId, that.gestionnaireId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statut, refArduino, dateCrea, libelle, marque, type, employeId, gestionnaireId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipementCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (statut != null ? "statut=" + statut + ", " : "") +
            (refArduino != null ? "refArduino=" + refArduino + ", " : "") +
            (dateCrea != null ? "dateCrea=" + dateCrea + ", " : "") +
            (libelle != null ? "libelle=" + libelle + ", " : "") +
            (marque != null ? "marque=" + marque + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (employeId != null ? "employeId=" + employeId + ", " : "") +
            (gestionnaireId != null ? "gestionnaireId=" + gestionnaireId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
