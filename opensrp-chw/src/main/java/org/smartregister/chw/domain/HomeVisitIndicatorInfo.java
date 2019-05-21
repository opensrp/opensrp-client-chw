package org.smartregister.chw.domain;

public class HomeVisitIndicatorInfo {

    private int homeVisitId;
    private String service;
    private String serviceDate;
    private String status;
    private String baseEntityId;
    private String eventType;
    private long updatedAt;

    public HomeVisitIndicatorInfo() {

    }

    public int getHomeVisitId() {
        return homeVisitId;
    }

    public void setHomeVisitId(int homeVisitId) {
        this.homeVisitId = homeVisitId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
