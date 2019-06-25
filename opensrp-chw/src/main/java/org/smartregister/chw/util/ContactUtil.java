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

            LocalDate expectedDeliveryDate = lastMenstrualPeriod.plusDays(280);

            // gets the list of contacts
            List<Integer> weeks = ChwApplication.getInstance().getRulesEngineHelper()
                    .getContactVisitSchedule(contactRule, Constants.RULE_FILE.CONTACT_RULES);

            boolean visitAfterEdd = false;
            for (Integer i : weeks) {

                LocalDate visitDate = lastMenstrualPeriod.plusWeeks(i).plusDays(1);
                if (visitDate.isBefore(expectedDeliveryDate) || visitDate.isEqual(expectedDeliveryDate)) {
                    dateMap.put(i, visitDate);
                } else {
                    visitAfterEdd = true;
                }
            }

            // remove a day form the last day
            if (visitAfterEdd) {
                dateMap.put(weeks.get(dateMap.size()), expectedDeliveryDate);
            }

            return dateMap;
        } catch (Exception e) {
            Timber.e(e);
        }

        return new LinkedHashMap<>();
    }

}
