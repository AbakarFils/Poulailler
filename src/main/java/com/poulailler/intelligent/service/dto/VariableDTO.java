package com.poulailler.intelligent.service.dto;

import com.poulailler.intelligent.domain.User;
import java.time.ZonedDateTime;

public class VariableDTO {

    private Long id;

    private String type;

    private Long plageMax;

    private ZonedDateTime dateCreation;

    private Boolean lue;

    private User consulter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPlageMax() {
        return plageMax;
    }

    public void setPlageMax(Long plageMax) {
        this.plageMax = plageMax;
    }

    public ZonedDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(ZonedDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public User getConsulter() {
        return consulter;
    }

    public void setConsulter(User consulter) {
        this.consulter = consulter;
    }

    public VariableDTO() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
