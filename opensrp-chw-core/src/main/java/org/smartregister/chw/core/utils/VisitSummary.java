package org.smartregister.chw.core.utils;

import org.smartregister.chw.core.model.ChildVisit;

public class VisitSummary extends ChildVisit {
    private String noOfDaysDue;

    public String getNoOfDaysDue() {
        return noOfDaysDue;
    }

    public void setNoOfDaysDue(String noOfDaysDue) {
        this.noOfDaysDue = noOfDaysDue;
    }
}
