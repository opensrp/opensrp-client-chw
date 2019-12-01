package org.smartregister.brac.hnpp.utils;

public class VisitLog {

    public String visitId;
    public String visitType;
    public String baseEntityId;
    public long visitDate;
    public String eventType;
    public String visitJson;

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public long getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(long visitDate) {
        this.visitDate = visitDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getVisitJson() {
        return visitJson;
    }

    public void setVisitJson(String visitJson) {
        this.visitJson = visitJson;
    }
}
