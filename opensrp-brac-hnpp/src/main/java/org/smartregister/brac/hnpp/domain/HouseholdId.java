package org.smartregister.brac.hnpp.domain;

import java.util.Date;

public class HouseholdId {
    private String id;
    private String openmrsId;
    private String villageId;
    private String status;
    private String usedBy;
    private Date createdAt;
    private Date updatedAt;

    public HouseholdId() {
    }

    public HouseholdId(String id, String villageId, String openmrsId, String status, String usedBy, Date createdAt) {
        this.id = id;
        this.openmrsId = openmrsId;
        this.status = status;
        this.usedBy = usedBy;
        this.createdAt = createdAt;
        this.villageId = villageId;
    }

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenmrsId() {
        return openmrsId;
    }

    public void setOpenmrsId(String openmrsId) {
        this.openmrsId = openmrsId;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
