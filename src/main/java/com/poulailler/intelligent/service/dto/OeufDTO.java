package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Oeuf} entity.
 */
public class OeufDTO implements Serializable {

    private Long id;

    private Long nombreJournalier;

    private VariableDTO variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNombreJournalier() {
        return nombreJournalier;
    }

    public void setNombreJournalier(Long nombreJournalier) {
        this.nombreJournalier = nombreJournalier;
    }

    public VariableDTO getVariable() {
        return variable;
    }

    public void setVariable(VariableDTO variable) {
        this.variable = variable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OeufDTO)) {
            return false;
        }

        OeufDTO oeufDTO = (OeufDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, oeufDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OeufDTO{" +
            "id=" + getId() +
            ", nombreJournalier=" + getNombreJournalier() +
            ", variable=" + getVariable() +
            "}";
    }
}
