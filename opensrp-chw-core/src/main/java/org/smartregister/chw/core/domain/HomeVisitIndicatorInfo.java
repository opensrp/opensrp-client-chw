package org.smartregister.chw.core.domain;

import java.util.Date;

/**
 * A class that models the home visit info required for processing home visit
 * related indicators
 *
 * @author Allan
 */
public class HomeVisitIndicatorInfo {

    private long homeVisitId;
    private String baseEntityId;
    private String service;
    private Date serviceDate;
    private Date serviceUpdateDate;
    private boolean serviceGiven;
    private Date lastHomeVisitDate;
    private String value;
    private Date updatedAt;
    private Date createdAt;

    public long getHomeVisitId() {
        return homeVisitId;
    }

    public void setHomeVisitId(long homeVisitId) {
        this.homeVisitId = homeVisitId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Date getServiceUpdateDate() {
        return serviceUpdateDate;
    }

    public void setServiceUpdateDate(Date serviceUpdateDate) {
        this.serviceUpdateDate = serviceUpdateDate;
    }

    public boolean isServiceGiven() {
        return serviceGiven;
    }

    public void setServiceGiven(boolean serviceGiven) {
        this.serviceGiven = serviceGiven;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getLastHomeVisitDate() {
        return lastHomeVisitDate;
    }

    public void setLastHomeVisitDate(Date lastHomeVisitDate) {
        this.lastHomeVisitDate = lastHomeVisitDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
