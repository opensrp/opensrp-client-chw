package org.smartgresiter.wcaro.util;

public class ChildVisit {
    String visitStatus;
    long lastVisitTime;
    String lastVisitMonth;
    String lastVisitDays;
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
