package com.opensrp.chw.core.domain;

import java.util.Date;

public class HomeVisitServiceDataModel {
    private String homeVisitId;
    private Date homeVisitDate;
    private String homeVisitDetails;
    private String eventType;

    public String getHomeVisitId() {
        return homeVisitId;
    }

    public void setHomeVisitId(String homeVisitId) {
        this.homeVisitId = homeVisitId;
    }

    public Date getHomeVisitDate() {
        return homeVisitDate;
    }

    public void setHomeVisitDate(Date homeVisitDate) {
        this.homeVisitDate = homeVisitDate;
    }

    public String getHomeVisitDetails() {
        return homeVisitDetails;
    }

    public void setHomeVisitDetails(String homeVisitDetails) {
        this.homeVisitDetails = homeVisitDetails;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
