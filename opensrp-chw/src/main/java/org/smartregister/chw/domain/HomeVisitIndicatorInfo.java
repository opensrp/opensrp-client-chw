package org.smartregister.chw.domain;

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
    private String serviceDate;
    private String serviceUpdateDate;
    private long lastHomeVisitDate;
    private long updatedAt;

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

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceUpdateDate() {
        return serviceUpdateDate;
    }

    public void setServiceUpdateDate(String serviceUpdateDate) {
        this.serviceUpdateDate = serviceUpdateDate;
    }

    public long getLastHomeVisitDate() {
        return lastHomeVisitDate;
    }

    public void setLastHomeVisitDate(long lastHomeVisitDate) {
        this.lastHomeVisitDate = lastHomeVisitDate;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
