package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.poulailler.intelligent.domain.NH3} entity.
 */
public class NH3DTO implements Serializable {

    private Long id;

    private Long volume;

    private VariableDTO variable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
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
        if (!(o instanceof NH3DTO)) {
            return false;
        }

        NH3DTO nH3DTO = (NH3DTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, nH3DTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NH3DTO{" +
            "id=" + getId() +
            ", volume=" + getVolume() +
            ", variable=" + getVariable() +
            "}";
    }
}
