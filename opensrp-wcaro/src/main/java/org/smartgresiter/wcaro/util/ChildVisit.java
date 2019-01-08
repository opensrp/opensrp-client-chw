package org.smartgresiter.wcaro.util;

public class ChildVisit {
    private String visitStatus;
    private long lastVisitTime;
    private String lastVisitMonth;
    private String lastVisitDays;
    private boolean visitNotDone;

    public boolean isVisitNotDone() {
        return visitNotDone;
    }

    public void setVisitNotDone(boolean visitNotDone) {
        this.visitNotDone = visitNotDone;
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
