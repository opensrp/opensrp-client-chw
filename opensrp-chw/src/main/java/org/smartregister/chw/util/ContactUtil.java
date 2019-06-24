package org.smartregister.chw.util;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.rule.ContactRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ContactUtil {

    /**
     * Returns the contact schedule in weeks for the given contact
     *
     * @param lastContact
     * @param lastMenstrualPeriod
     * @return
     */
    public static Map<Integer, LocalDate> getContactWeeks(Boolean isFirst, LocalDate lastContact, LocalDate lastMenstrualPeriod) {

        try {

            int gestationAge = Days.daysBetween(lastMenstrualPeriod, lastContact).getDays() / 7;

            Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();
            ContactRule contactRule = new ContactRule(gestationAge, isFirst);

            // gets the list of contacts
            List<Integer> weeks = ChwApplication.getInstance().getRulesEngineHelper()
                    .getContactVisitSchedule(contactRule, Constants.RULE_FILE.CONTACT_RULES);

            for (Integer i : weeks) {
                dateMap.put(i, lastMenstrualPeriod.plusWeeks(i));
            }

            return dateMap;
        } catch (Exception e) {
            Timber.e(e);
        }

        return new LinkedHashMap<>();
    }

}
