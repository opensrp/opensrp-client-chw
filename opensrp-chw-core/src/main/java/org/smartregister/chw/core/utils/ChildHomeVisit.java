package org.smartregister.chw.core.utils;

public class ChildHomeVisit {

    private long lastHomeVisitDate;

    private long visitNotDoneDate;

    private long dateCreated;

    public long getLastHomeVisitDate() {
        return lastHomeVisitDate;
    }

    public void setLastHomeVisitDate(long lastHomeVisitDate) {
        this.lastHomeVisitDate = lastHomeVisitDate;
    }

    public long getVisitNotDoneDate() {
        return visitNotDoneDate;
    }

    public void setVisitNotDoneDate(long visitNotDoneDate) {
        this.visitNotDoneDate = visitNotDoneDate;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
