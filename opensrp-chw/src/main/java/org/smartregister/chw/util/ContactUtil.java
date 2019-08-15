package org.smartregister.chw.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.helper.ContactRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ContactUtil {

    public static Map<Integer, LocalDate> getContactSchedule(MemberObject memberObject) {
        return getContactSchedule(memberObject, LocalDate.now());
    }

    public static Map<Integer, LocalDate> getContactSchedule(MemberObject memberObject, LocalDate startDate) {

        LocalDate lastContact = new DateTime(memberObject.getDateCreated()).toLocalDate();
        boolean isFirst = (StringUtils.isBlank(memberObject.getLastContactVisit()));
        LocalDate lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastMenstrualPeriod());

        if (StringUtils.isNotBlank(memberObject.getLastContactVisit()))
            lastContact = DateTimeFormat.forPattern("dd-MM-yyyy").parseLocalDate(memberObject.getLastContactVisit());

        Map<Integer, LocalDate> dateMap = new LinkedHashMap<>();

        // today is the due date for the very first visit
        if (isFirst) {
            dateMap.put(0, startDate);
        }

        dateMap.putAll(ContactUtil.getContactWeeks(isFirst, lastContact, lastMenstrualPeriod));

        return dateMap;
    }

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
