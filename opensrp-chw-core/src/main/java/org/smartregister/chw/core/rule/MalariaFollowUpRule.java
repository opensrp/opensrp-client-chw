package org.smartregister.chw.core.rule;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;

public class MalariaFollowUpRule implements ICommonRule {

    public static final String RULE_KEY = "malariaFollowUpRule";

    private Date testDate;
    private String buttonStatus = "";

    public MalariaFollowUpRule(Date testDate) {
        this.testDate = testDate;
    }

    public int getDatesDiff() {
        return Days.daysBetween(new DateTime(testDate), new DateTime()).getDays();
    }

    public void setButtonStatus(String buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    @Override
    public String getRuleKey() {
        return "malariaFollowUpRule";
    }

    @Override
    public String getButtonStatus() {
        return buttonStatus;
    }
}
