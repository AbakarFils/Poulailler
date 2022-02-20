package com.poulailler.intelligent.service.dto;

import java.io.Serializable;
import java.util.Objects;

public class EmployeDTO implements Serializable {

    private Long id;

    private UserDTO user;

    private String adresse;

    private String numeroIdentite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeDTO)) return false;
        EmployeDTO that = (EmployeDTO) o;
        return (
            id.equals(that.id) &&
            user.equals(that.user) &&
            Objects.equals(adresse, that.adresse) &&
            numeroIdentite.equals(that.numeroIdentite)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, adresse, numeroIdentite);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumeroIdentite() {
        return numeroIdentite;
    }

    public void setNumeroIdentite(String numeroIdentite) {
        this.numeroIdentite = numeroIdentite;
    }
}
