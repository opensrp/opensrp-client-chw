package org.smartregister.chw.core.domain;

public class AlertState {
    private String caseID;
    private String startDate;
    private String visitCode;
    private String dateGiven;

    public AlertState(String caseID, String startDate, String visitCode, String dateGiven) {
        this.caseID = caseID;
        this.startDate = startDate;
        this.visitCode = visitCode;
        this.dateGiven = dateGiven;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getVisitCode() {
        return visitCode;
    }

    public void setVisitCode(String visitCode) {
        this.visitCode = visitCode;
    }

    public String getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(String dateGiven) {
        this.dateGiven = dateGiven;
    }
}
