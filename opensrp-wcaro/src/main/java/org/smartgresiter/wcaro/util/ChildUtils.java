package org.smartgresiter.wcaro.util;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.domain.HomeVisit;
import org.smartgresiter.wcaro.repository.HomeVisitRepository;
import org.smartgresiter.wcaro.rule.HomeAlertRule;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildUtils {

    private static final String[] firstSecondNumber = {"Zero", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th"};
    public static final String[] ONE_YR = {"bcg",
            "hepb", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "rota3", "ipv", "mcv1",
            "yf"
    };
    public static final String[] TWO_YR = {"bcg",
            "hepb", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "rota3", "ipv", "mcv1",
            "yf", "mcv2"
    };

    //Fully immunized at age 2
    public static String isFullyImmunized(int age, List<String> vaccineGiven) {
        String str = "";
        if (age < 1) {
            List<String> oneYrVac = Arrays.asList(ONE_YR);
            if (vaccineGiven.containsAll(oneYrVac)) {
                str = "1";
            }
        } else {
            List<String> twoYrVac = Arrays.asList(TWO_YR);
            if (vaccineGiven.containsAll(twoYrVac)) {
                str = "2";
            }
        }

        return str;

    }

    public static Object[] getStringWithNumber(String fullString) {
        Object[] objects = new Object[2];
        if (fullString.length() > 0) {
            fullString = StringUtils.capitalize(fullString);
            String str = "";
            String digit = "";
            for (int i = 0; i < fullString.length(); i++) {
                char c = fullString.charAt(i);
                if (Character.isDigit(c)) {
                    digit = digit + c;
                } else {
                    str = str + c;
                }

            }
            objects[0] = str;
            objects[1] = digit;
        }
        return objects;
    }

    public static String getFirstSecondAsNumber(String number) {
        try {
            int index = Integer.parseInt(number);
            return firstSecondNumber[index];
        } catch (Exception e) {

        }
        return "";

    }

    public static String mainSelectRegisterWithoutGroupby(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName, familyTableName, familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver");

        return queryBUilder.mainCondition(mainCondition);
    }

    public static String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {

        String query = mainSelectRegisterWithoutGroupby(tableName, familyTableName, familyMemberTableName, tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + mainCondition + "'");


        return query;
    }

    public static String getChildListByFamilyId(String tableName, String familyId, String childId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{DBConstants.KEY.BASE_ENTITY_ID});
        String query = queryBUilder.mainCondition(tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = '" + familyId + "'");
        return query;
    }

    public static ChildHomeVisit getLastHomeVisit(String tableName, String childId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE});
        String query = queryBUilder.mainCondition(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + childId + "'");

        ChildHomeVisit childHomeVisit = new ChildHomeVisit();
        Cursor cursor = Utils.context().commonrepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).queryTable(query);
        if (cursor != null && cursor.moveToFirst()) {
            String lastVisitStr = cursor.getString(cursor.getColumnIndex(ChildDBConstants.KEY.LAST_HOME_VISIT));
            if (!TextUtils.isEmpty(lastVisitStr)) {
                try {
                    childHomeVisit.setLastHomeVisitDate(Long.parseLong(lastVisitStr));
                } catch (Exception e) {

                }
            }
            String visitNotDoneStr = cursor.getString(cursor.getColumnIndex(ChildDBConstants.KEY.VISIT_NOT_DONE));
            if (!TextUtils.isEmpty(visitNotDoneStr)) {
                try {
                    childHomeVisit.setVisitNotDoneDate(Long.parseLong(visitNotDoneStr));
                } catch (Exception e) {

                }
            }
            cursor.close();
        }

        return childHomeVisit;
    }

    private static String[] mainColumns(String tableName, String familyTable, String familyMemberTable) {

        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID,
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + ChildDBConstants.KEY.FAMILY_FIRST_NAME,
                familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + ChildDBConstants.KEY.FAMILY_LAST_NAME,
                familyTable + "." + DBConstants.KEY.VILLAGE_TOWN + " as " + ChildDBConstants.KEY.FAMILY_HOME_ADDRESS,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.UNIQUE_ID,
                tableName + "." + DBConstants.KEY.GENDER,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT,
                tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NUMBER,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_DATE,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_DESCRIPTION,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_ACTION};
        return columns;
    }

    /**
     * Same thread to retrive rules and also save in fts
     *
     * @param yearOfBirth
     * @param lastVisitDate
     * @param visitNotDate
     * @return
     */
    public static ChildVisit getChildVisitStatus(String yearOfBirth, long lastVisitDate, long visitNotDate) {
        HomeAlertRule homeAlertRule = new HomeAlertRule(yearOfBirth, lastVisitDate, visitNotDate);
        WcaroApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, Constants.RULE_FILE.HOME_VISIT);
        return getChildVisitStatus(homeAlertRule, lastVisitDate);
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
    public static ChildVisit getChildVisitStatus(Rules rules, String yearOfBirth, long lastVisitDate, long visitNotDate) {
        HomeAlertRule homeAlertRule = new HomeAlertRule(yearOfBirth, lastVisitDate, visitNotDate);
        WcaroApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, rules);
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

    @SuppressLint("SimpleDateFormat")
    public static String covertLongDateToDisplayDate(long callingTime) {
        Date date = new Date(callingTime);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateText2 = df2.format(date);
        return dateText2;

    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    //event type="Child Home Visit"/Visit not done
    public static void updateClientStatusAsEvent(String entityId, String eventType, String attributeName, Object attributeValue, String entityType) {
        try {

            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());
            event.addObs((new Obs()).withFormSubmissionField(attributeName).withValue(attributeValue).withFieldCode(attributeName).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            JsonFormUtils.tagSyncMetadata(WcaroApplication.getInstance().getContext().allSharedPreferences(), event);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = WcaroApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            FamilyLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            WcaroApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            //update details


        } catch (Exception e) {
            Log.e("Error in adding event", e.getMessage());
        }
    }

    //event type="Child Home Visit"/Visit not done
    public static void updateHomeVisitAsEvent(String entityId, String eventType, String entityType, JSONObject singleVaccineObject, JSONObject vaccineGroupObject, JSONObject service, String birthCert, JSONObject illnessJson) {
        try {

            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());

            event.addObs((new Obs()).withFormSubmissionField("singleVaccine").withValue(singleVaccineObject.toString()).withFieldCode("singleVaccine").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("groupVaccine").withValue(vaccineGroupObject.toString()).withFieldCode("groupVaccine").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("service").withValue(service.toString()).withFieldCode("service").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("birth_certificate").withValue(birthCert).withFieldCode("birth_certificate").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("illness_information").withValue(illnessJson.toString()).withFieldCode("illness_information").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));

            JsonFormUtils.tagSyncMetadata(WcaroApplication.getInstance().getContext().allSharedPreferences(), event);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = WcaroApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            WcaroApplication.getClientProcessor(WcaroApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            WcaroApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            //update details


        } catch (Exception e) {
            Log.e("Error in adding event", e.getMessage());
        }
    }

    public static SpannableString daysAway(String dueDate) {
        SpannableString spannableString;
        LocalDate date1 = new LocalDate(dueDate);
        LocalDate date2 = new LocalDate();
        int diff = Days.daysBetween(date1, date2).getDays();
        if (diff < 0) {
            String str = diff + " days away";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            String str = diff + " days overdue";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public static void addToHomeVisitTable(String baseEntityID,List<org.smartregister.domain.db.Obs> observations ) {
        HomeVisit newHomeVisit = new HomeVisit(null,baseEntityID, HomeVisitRepository.EVENT_TYPE,new Date(),"","","",0l,"","",new Date());
        try {
            for (org.smartregister.domain.db.Obs obs : observations) {
                if (obs.getFormSubmissionField().equalsIgnoreCase("singleVaccine")) {
                    newHomeVisit.setSingleVaccinesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("groupVaccine")) {
                    newHomeVisit.setVaccineGroupsGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("service")) {
                    newHomeVisit.setServicesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("birth_certificate")) {
                    newHomeVisit.setBirthCertificationState((String)obs.getValue());
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("illness_information")) {
                    newHomeVisit.setIllness_information(new JSONObject((String) obs.getValue()));
                }
            }
        }catch (Exception e){

        }
        newHomeVisit.setFormfields(new HashMap<String, String>());
        WcaroApplication.homeVisitRepository().add(newHomeVisit);
    }

    public static void addToHomeVisitTable(String baseEntityID, JSONObject singleVaccineObject, JSONObject vaccineGroupObject, JSONObject service, String birthCert, JSONObject illnessJson) {
        HomeVisit newHomeVisit = new HomeVisit(null,baseEntityID, HomeVisitRepository.EVENT_TYPE,new Date(),"","","",0l,"","",new Date());
        newHomeVisit.setSingleVaccinesGiven(singleVaccineObject);
        newHomeVisit.setVaccineGroupsGiven(vaccineGroupObject);
        newHomeVisit.setServicesGiven(service);
        newHomeVisit.setBirthCertificationState(birthCert);
        if(illnessJson!=null){
            newHomeVisit.setIllness_information(illnessJson);
        }
        newHomeVisit.setFormfields(new HashMap<String, String>());
        WcaroApplication.homeVisitRepository().add(newHomeVisit);
    }


}
