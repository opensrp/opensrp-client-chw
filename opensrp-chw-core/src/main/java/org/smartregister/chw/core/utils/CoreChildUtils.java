package org.smartregister.chw.core.utils;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.domain.VisitSummary;
import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.rule.HomeAlertRule;
import org.smartregister.chw.core.rule.ImmunizationExpiredRule;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.BaseRepository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public abstract class CoreChildUtils {
    public static final String[] firstSecondNumber = {"Zero", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th"};
    public static Gson gsonConverter;

    static {
        gsonConverter = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>) (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json)))
                .registerTypeAdapter(DateTime.class, (JsonDeserializer<DateTime>) (json, typeOfT, context) -> new DateTime(json.getAsJsonPrimitive().getAsString()))
                .create();
    }

    public static SpannableString daysAway(String dueDate) {
        SpannableString spannableString;
        LocalDate date1 = new LocalDate(dueDate);
        LocalDate date2 = new LocalDate();
        int diff = Days.daysBetween(date1, date2).getDays();
        if (diff <= 0) {
            String str = Math.abs(diff) + " days away";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(CoreChwApplication.getInstance().getContext().getColorResource(R.color.grey)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            String str = diff + " days overdue";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(CoreChwApplication.getInstance().getContext().getColorResource(R.color.alert_urgent_red)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public static String getChildListByFamilyId(String tableName, String familyId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{DBConstants.KEY.BASE_ENTITY_ID});
        return queryBUilder.mainCondition(MessageFormat.format("{0}.{1} = ''{2}''", tableName, DBConstants.KEY.RELATIONAL_ID, familyId));
    }

    public static ChildHomeVisit getLastHomeVisit(String tableName, String childId) {

        ChildHomeVisit childHomeVisit = new ChildHomeVisit();
        Map<String, VisitSummary> map = VisitDao.getVisitSummary(childId);
        if (map == null) {
            return childHomeVisit;
        }

        VisitSummary notDone = map.get(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);
        VisitSummary lastVisit = map.get(CoreConstants.EventType.CHILD_HOME_VISIT);

        if (lastVisit != null) {
            childHomeVisit.setLastHomeVisitDate(lastVisit.getVisitDate().getTime());
        }

        if (notDone != null) {
            childHomeVisit.setVisitNotDoneDate(notDone.getVisitDate().getTime());
        }


        Long datecreated = VisitDao.getChildDateCreated(childId);
        if (datecreated != null) {
            childHomeVisit.setDateCreated(datecreated);
        }

        return childHomeVisit;
    }

    public static String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        return mainSelectRegisterWithoutGroupby(tableName, familyTableName, familyMemberTableName, tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + mainCondition + "'");
    }

    public static String mainSelectRegisterWithoutGroupby(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver COLLATE NOCASE ");

        return queryBUilder.mainCondition(mainCondition);
    }

    public static String[] mainColumns(String tableName, String familyTable, String familyMemberTable) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(tableName + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + ChildDBConstants.KEY.FAMILY_FIRST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + ChildDBConstants.KEY.FAMILY_LAST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME + " as " + ChildDBConstants.KEY.FAMILY_MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + ChildDBConstants.PHONE_NUMBER + " as " + ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER);
        columnList.add(familyMemberTable + "." + ChildDBConstants.OTHER_PHONE_NUMBER + " as " + ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER_OTHER);
        columnList.add(familyTable + "." + DBConstants.KEY.VILLAGE_TOWN + " as " + ChildDBConstants.KEY.FAMILY_HOME_ADDRESS);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(tableName + "." + DBConstants.KEY.GENDER);
        columnList.add(tableName + "." + DBConstants.KEY.DOB);
        columnList.add(tableName + "." + org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN);
        columnList.add(tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT);
        columnList.add(tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.CHILD_BF_HR);
        columnList.add(tableName + "." + ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NUMBER);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_DATE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_DESCRIPTION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.DATE_CREATED);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_ACTION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.VACCINE_CARD);
        return columnList.toArray(new String[columnList.size()]);
    }

    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static Integer dobStringToYear(String yearOfBirthString) {
        if (!TextUtils.isEmpty(yearOfBirthString)) {
            try {
                String year = yearOfBirthString.contains("y") ? yearOfBirthString.substring(0, yearOfBirthString.indexOf("y")) : "";
                if (StringUtils.isNotBlank(year)) {
                    return Integer.valueOf(year);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String getImmunizationExpired(String dateOfBirth, String vaccineName) {
        //String dob = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        ImmunizationExpiredRule immunizationExpiredRule = new ImmunizationExpiredRule(dateOfBirth, vaccineName);
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(immunizationExpiredRule, CoreConstants.RULE_FILE.IMMUNIZATION_EXPIRED);
        return immunizationExpiredRule.getButtonStatus();
    }

    public static ImmunizationState getDueStatus(String dueDate) {
        LocalDate date1 = new LocalDate(dueDate);
        LocalDate date2 = new LocalDate();
        int diff = Days.daysBetween(date1, date2).getDays();
        return diff <= 0 ? ImmunizationState.UPCOMING : ImmunizationState.OVERDUE;
    }

    /**
     * This method return the vaccine name first character as capital for selected vaccines
     * other's return as capital form like : input opv 1 output OPV 1,rota 1 as Rota 1,mena as MenA
     *
     * @param "opv 1","rota 1","mena"
     * @return "OPV 1","Rota 1","MenA"
     */

    public static String fixVaccineCasing(String display) {
        String vaccineDisplay = display;
        vaccineDisplay = vaccineDisplay.toUpperCase();
        if (vaccineDisplay.toLowerCase().contains("mena")) {
            return "MenA";
        }
        if (vaccineDisplay.toLowerCase().contains("rota")
                || display.toLowerCase().contains("penta")
                || display.toLowerCase().contains("yellow fever")
                || display.toLowerCase().contains("rubella")) {
            vaccineDisplay = WordUtils.capitalize(vaccineDisplay.toLowerCase());
        }

        return vaccineDisplay;
    }

    public static Object[] getStringWithNumber(String fullString) {
        Object[] objects = new Object[2];
        if (fullString.length() > 0) {
            String formattedName = StringUtils.capitalize(fullString);
            StringBuilder str = new StringBuilder();
            StringBuilder digit = new StringBuilder();
            for (int i = 0; i < formattedName.length(); i++) {
                char c = formattedName.charAt(i);
                if (Character.isDigit(c)) {
                    digit.append(c);
                } else {
                    str.append(c);
                }

            }
            objects[0] = str.toString();
            objects[1] = digit.toString();
        }
        return objects;
    }

    public static String getFirstSecondAsNumber(String number, Context context) {
        try {
            int index = Integer.parseInt(number);
            switch (index) {
                case 1:
                    return context.getString(R.string.abv_first);
                case 2:
                    return context.getString(R.string.abv_second);
                case 3:
                    return context.getString(R.string.abv_third);
                case 4:
                    return context.getString(R.string.abv_fourth);
                case 5:
                    return context.getString(R.string.abv_fifth);
                case 6:
                    return context.getString(R.string.abv_sixth);
                case 7:
                    return context.getString(R.string.abv_seventh);
                case 8:
                    return context.getString(R.string.abv_eigth);
                case 9:
                    return context.getString(R.string.abv_nineth);
                case 10:
                    return context.getString(R.string.abv_tenth);
                case 11:
                    return context.getString(R.string.abv_eleventh);
                case 12:
                    return context.getString(R.string.abv_twelfth);
                default:
                    return "";
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";

    }

    /**
     * Add visit not done to visits table
     *
     * @param entityId
     */
    public static void visitNotDone(String entityId) {
        try {
            Event event = JsonFormUtils.createUntaggedEvent(entityId, CoreConstants.EventType.CHILD_VISIT_NOT_DONE, Constants.TABLES.EC_CHILD);
            Visit visit = NCUtils.eventToVisit(event, JsonFormUtils.generateRandomUUIDString());
            visit.setPreProcessedJson(new Gson().toJson(event));
            AncLibrary.getInstance().visitRepository().addVisit(visit);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /**
     * remove visit not done from visits table
     *
     * @param entityId
     */
    public static void undoVisitNotDone(String entityId) {
        // deletes the last visit not done event if it was create less than 24hrs ago
        VisitDao.undoChildVisitNotDone(entityId);
    }

    public static void processClientProcessInBackground() {
        try {
            long lastSyncTimeStamp = CoreChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            CoreChwApplication.getClientProcessor(CoreChwApplication.getInstance().getContext().applicationContext()).processClient(FamilyLibrary.getInstance().getEcSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            CoreChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    /**
     * Same thread to retrive rules and also updateFamilyRelations in fts
     *
     * @param yearOfBirth
     * @param lastVisitDate
     * @param visitNotDate
     * @return
     */
    public static ChildVisit getChildVisitStatus(Context context, String yearOfBirth, long lastVisitDate, long visitNotDate, long dateCreated) {
        HomeAlertRule homeAlertRule = new HomeAlertRule(context, yearOfBirth, lastVisitDate, visitNotDate, dateCreated);
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, CoreConstants.RULE_FILE.HOME_VISIT);
        return getChildVisitStatus(homeAlertRule, lastVisitDate);
    }

    public static ChildVisit getChildVisitStatus(HomeAlertRule homeAlertRule, long lastVisitDate) {
        ChildVisit childVisit = new ChildVisit();
        childVisit.setVisitStatus(homeAlertRule.buttonStatus);
        childVisit.setNoOfMonthDue(homeAlertRule.noOfMonthDue);
        childVisit.setLastVisitDays(homeAlertRule.noOfDayDue);
        childVisit.setLastVisitMonthName(homeAlertRule.visitMonthName);
        childVisit.setLastVisitTime(lastVisitDate);
        return childVisit;
    }

    /**
     * Rules can be retrieved separately so that the background thread is used here
     *
     * @param rules
     * @param yearOfBirth
     * @param lastVisitDate
     * @param visitNotDate
     * @return
     */
    public static ChildVisit getChildVisitStatus(Context context, Rules rules, String yearOfBirth, long lastVisitDate, long visitNotDate, long dateCreated) {
        HomeAlertRule homeAlertRule = new HomeAlertRule(context, yearOfBirth, lastVisitDate, visitNotDate, dateCreated);
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, rules);
        return getChildVisitStatus(homeAlertRule, lastVisitDate);
    }

    public static ServiceTask createServiceTaskFromEvent(String taskType, String details, String title, String formSubmissionId) {
        ServiceTask serviceTask = new ServiceTask();
        Event event = CoreChildUtils.gsonConverter.fromJson(details, new TypeToken<Event>() {
        }.getType());
        List<org.smartregister.clientandeventmodel.Obs> observations = event.getObs();
        for (org.smartregister.clientandeventmodel.Obs obs : observations) {
            if (obs.getFormSubmissionField().equalsIgnoreCase(formSubmissionId)) {
                List<Object> hu = obs.getHumanReadableValues();
                String value = "";
                for (Object object : hu) {
                    value = (String) object;
                }
                serviceTask.setTaskLabel(value);
            }
        }
        serviceTask.setTaskTitle(title);
        serviceTask.setTaskType(taskType);
        return serviceTask;

    }
}
