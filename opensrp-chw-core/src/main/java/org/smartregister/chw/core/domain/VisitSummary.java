package org.smartregister.chw.core.domain;

import java.util.Date;

public class VisitSummary {
    private String visitType;
    private Date visitDate;
    private String baseEntityID;

    public VisitSummary(String visitType, Date visitDate, String baseEntityID) {
        this.visitType = visitType;
        this.visitDate = visitDate;
        this.baseEntityID = baseEntityID;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }
}
