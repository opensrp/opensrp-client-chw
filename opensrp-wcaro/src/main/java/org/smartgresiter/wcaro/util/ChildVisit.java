package org.smartgresiter.wcaro.util;

public class ChildVisit {
    String visitStatus;
    long lastVisitTime;
    String lastVisitMonth;
    String lastVisitDays;
    String serviceName;
    String serviceStatus;
    String serviceDate;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }


    public String getLastVisitDays() {
        return lastVisitDays;
    }

    public void setLastVisitDays(String lastVisitDays) {
        this.lastVisitDays = lastVisitDays;
    }


    public String getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(String visitStatus) {
        this.visitStatus = visitStatus;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(long lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }


    public String getLastVisitMonth() {
        return lastVisitMonth;
    }

    public void setLastVisitMonth(String lastVisitMonth) {
        this.lastVisitMonth = lastVisitMonth;
    }




}
