package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.Temperature} entity.
 */
public class TemperatureDTO implements Serializable {

    private Long id;

    private Long dregree;

    private VariableDTO variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDregree() {
        return dregree;
    }

    public void setDregree(Long dregree) {
        this.dregree = dregree;
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
        if (!(o instanceof TemperatureDTO)) {
            return false;
        }

        TemperatureDTO temperatureDTO = (TemperatureDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, temperatureDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TemperatureDTO{" +
            "id=" + getId() +
            ", dregree=" + getDregree() +
            ", variable=" + getVariable() +
            "}";
    }
}
