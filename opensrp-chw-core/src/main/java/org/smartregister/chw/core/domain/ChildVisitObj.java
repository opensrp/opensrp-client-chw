package org.smartregister.chw.core.domain;

import java.util.Date;

public class ChildVisitObj {

    private Long Id;
    private String BaseEntityID;
    private String RelationalID;
    private Date DateVisited;
    private Date DateNotVisited;
    private Date CreatedAt;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getBaseEntityID() {
        return BaseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        BaseEntityID = baseEntityID;
    }

    public String getRelationalID() {
        return RelationalID;
    }

    public void setRelationalID(String relationalID) {
        RelationalID = relationalID;
    }

    public Date getDateVisited() {
        return DateVisited;
    }

    public void setDateVisited(Date dateVisited) {
        DateVisited = dateVisited;
    }

    public Date getDateNotVisited() {
        return DateNotVisited;
    }

    public void setDateNotVisited(Date dateNotVisited) {
        DateNotVisited = dateNotVisited;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        CreatedAt = createdAt;
    }

}
