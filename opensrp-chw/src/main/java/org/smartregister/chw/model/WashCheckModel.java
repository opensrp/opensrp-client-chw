package org.smartregister.chw.model;

import java.util.Date;

public class WashCheckModel {
    private String familyBaseEntityId;
    private long lastVisit;
    private String detailsJson;
    private String status;
    private long dateCreatedFamily;
    private String lastVisitDate;

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDateCreatedFamily() {
        return dateCreatedFamily;
    }

    public void setDateCreatedFamily(long dateCreatedFamily) {
        this.dateCreatedFamily = dateCreatedFamily;
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    @Override
    public String toString() {
        return "WashCheckModel{" +
                "familyBaseEntityId='" + familyBaseEntityId + '\'' +
                ", lastVisit=" + lastVisit +
                '}';
    }
}
