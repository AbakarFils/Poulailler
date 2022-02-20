package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Humidite} entity.
 */
public class HumiditeDTO implements Serializable {

    private Long id;

    private Long niveau;

    private VariableDTO variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNiveau() {
        return niveau;
    }

    public void setNiveau(Long niveau) {
        this.niveau = niveau;
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
        if (!(o instanceof HumiditeDTO)) {
            return false;
        }

        HumiditeDTO humiditeDTO = (HumiditeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, humiditeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HumiditeDTO{" +
            "id=" + getId() +
            ", niveau=" + getNiveau() +
            ", variable=" + getVariable() +
            "}";
    }
}
