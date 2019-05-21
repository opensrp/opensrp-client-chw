package org.smartregister.chw.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.repository.HomeVisitRepository;
import org.smartregister.chw.rule.BirthCertRule;
import org.smartregister.chw.rule.HomeAlertRule;
import org.smartregister.chw.rule.ServiceRule;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Alert;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.apache.commons.lang3.text.WordUtils.capitalize;
import static org.smartregister.chw.util.Utils.dd_MMM_yyyy;

public class ChildUtils {

    private static final String[] firstSecondNumber = {"Zero", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th"};
    private static final String[] ONE_YR = {"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mcv1",
            "yellowfever"
    };
    private static final String[] TWO_YR = {"bcg", "opv1", "penta1", "pcv1", "rota1", "opv2", "penta2", "pcv2", "rota2", "opv3", "penta3", "pcv3", "ipv", "mcv1",
            "yellowfever", "mcv2"
    };
    public static Gson gsonConverter;

    static {
        gsonConverter = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {
                    @Override
                    public JsonElement serialize(DateTime json, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(ISODateTimeFormat.dateTime().print(json));
                    }

                })
                .registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
                    @Override
                    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new DateTime(json.getAsJsonPrimitive().getAsString());
                    }
                })
                .create();
    }

    public static boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return true;
            }
        }
        return false;
    }

    public static ImmunizationState alertState(Alert toProcess) {
        if (toProcess == null) {
            return ImmunizationState.NO_ALERT;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
            return ImmunizationState.DUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
            return ImmunizationState.UPCOMING;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
            return ImmunizationState.OVERDUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
            return ImmunizationState.EXPIRED;
        }
        return ImmunizationState.NO_ALERT;
    }

    public static boolean isReceived(String s, Map<String, Date> receivedvaccines) {
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert.scheduleName().equalsIgnoreCase(vaccine.display())) {
                return alertState(alert);
            }
        }
        return ImmunizationState.NO_ALERT;
    }

    /**
     * Based on received vaccine list it'll return the fully immunized year.
     * Firstly it'll check with 2years vaccine list if it's match then return 2 year fully immunized.
     * Else it'll check  with 1year vaccine list otherwise it'll return empty string means not fully immunized.
     *
     * @param vaccineGiven
     * @return
     */
    public static String isFullyImmunized(List<String> vaccineGiven) {
        List<String> twoYrVac = Arrays.asList(TWO_YR);
        if (vaccineGiven.containsAll(twoYrVac)) {
            return "2";
        }

        List<String> oneYrVac = Arrays.asList(ONE_YR);
        if (vaccineGiven.containsAll(oneYrVac)) {
            return "1";
        }

        return "";

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
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + "." + DBConstants.KEY.RELATIONAL_ID + " = " + familyTableName + ".id COLLATE NOCASE ");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + familyMemberTableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " + familyTableName + ".primary_caregiver COLLATE NOCASE ");

        return queryBUilder.mainCondition(mainCondition);
    }

    public static String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition) {
        return mainSelectRegisterWithoutGroupby(tableName, familyTableName, familyMemberTableName, tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + mainCondition + "'");
    }

    public static String getChildListByFamilyId(String tableName, String familyId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{DBConstants.KEY.BASE_ENTITY_ID});
        return queryBUilder.mainCondition(MessageFormat.format("{0}.{1} = ''{2}''", tableName, DBConstants.KEY.RELATIONAL_ID, familyId));
    }

    public static ChildHomeVisit getLastHomeVisit(String tableName, String childId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED});
        String query = queryBUilder.mainCondition(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID + " = '" + childId + "'");

        ChildHomeVisit childHomeVisit = new ChildHomeVisit();
        Cursor cursor = null;
        try {
            cursor = Utils.context().commonrepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).queryTable(query);
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
                        Timber.e(e.toString());
                    }
                }
                String strDateCreated = cursor.getString(cursor.getColumnIndex(ChildDBConstants.KEY.DATE_CREATED));
                if (!TextUtils.isEmpty(strDateCreated)) {
                    try {
                        childHomeVisit.setDateCreated(org.smartregister.family.util.Utils.dobStringToDateTime(strDateCreated).getMillis());
                    } catch (Exception e) {
                        Timber.e(e.toString());
                    }
                }
            }
        } catch (Exception ex) {
            Timber.e(ex.toString());
        } finally {
            if (cursor != null)
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
                tableName + "." + DBConstants.KEY.MIDDLE_NAME,
                familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + ChildDBConstants.KEY.FAMILY_FIRST_NAME,
                familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + ChildDBConstants.KEY.FAMILY_LAST_NAME,
                familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME + " as " + ChildDBConstants.KEY.FAMILY_MIDDLE_NAME,
                familyTable + "." + DBConstants.KEY.VILLAGE_TOWN + " as " + ChildDBConstants.KEY.FAMILY_HOME_ADDRESS,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.UNIQUE_ID,
                tableName + "." + DBConstants.KEY.GENDER,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN,
                tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT,
                tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE,
                tableName + "." + ChildDBConstants.KEY.CHILD_BF_HR,
                tableName + "." + ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NUMBER,
                tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_DATE,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_DESCRIPTION,
                tableName + "." + ChildDBConstants.KEY.DATE_CREATED,
                tableName + "." + ChildDBConstants.KEY.ILLNESS_ACTION,
                tableName + "." + ChildDBConstants.KEY.VACCINE_CARD
        };
        return columns;
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
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, Constants.RULE_FILE.HOME_VISIT);
        return getChildVisitStatus(homeAlertRule, lastVisitDate);
    }

    public static String getBirthCertDueStatus(String dateOfBirth) {
        BirthCertRule birthCertRule = new BirthCertRule(dateOfBirth);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(birthCertRule, Constants.RULE_FILE.BIRTH_CERT);
        return birthCertRule.getButtonStatus();
    }

    public static String getServiceDueStatus(String dueDate) {
        ServiceRule serviceRule = new ServiceRule(dueDate);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(serviceRule, Constants.RULE_FILE.SERVICE);
        return serviceRule.getButtonStatus();
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
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, rules);
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

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    //event type="Child Home Visit"/Visit not done
    public static void updateHomeVisitAsEvent(String entityId, String eventType, String entityType, JSONObject singleVaccineObject, JSONObject vaccineGroupObject, JSONObject vaccineNotGiven, JSONObject service, JSONObject serviceNotGiven, JSONObject birthCert, JSONObject illnessJson, String visitStatus, String value) {
        try {

            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());


            event.addObs((new Obs()).withFormSubmissionField(visitStatus).withValue(value).withFieldCode(visitStatus).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));

            event.addObs((new Obs()).withFormSubmissionField("singleVaccine").withValue(singleVaccineObject.toString()).withFieldCode("singleVaccine").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("groupVaccine").withValue(vaccineGroupObject.toString()).withFieldCode("groupVaccine").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("vaccineNotGiven").withValue(vaccineNotGiven.toString()).withFieldCode("vaccineNotGiven").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));

            event.addObs((new Obs()).withFormSubmissionField("service").withValue(service.toString()).withFieldCode("service").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("serviceNotGiven").withValue(serviceNotGiven.toString()).withFieldCode("serviceNotGiven").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));

            event.addObs((new Obs()).withFormSubmissionField("birth_certificate").withValue(birthCert.toString()).withFieldCode("birth_certificate").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));
            event.addObs((new Obs()).withFormSubmissionField("illness_information").withValue(illnessJson.toString()).withFieldCode("illness_information").withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<Object>()));

            JsonFormUtils.tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), event);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            //update details


        } catch (Exception e) {
            Log.e("Error in adding event", e.getMessage());
        }
    }
    public static void updateVaccineCardAsEvent(Context context,String entityId,String choiceValue){
        try{
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEntityType(Constants.TABLE_NAME.CHILD)
                    .withEventType(Constants.EventType.VACCINE_CARD_RECEIVED)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());
            List<Object> huValue = new ArrayList<>();
            huValue.add(choiceValue);

            baseEvent.addObs(new Obs("concept", "text",Constants.FORM_CONSTANTS.VACCINE_CARD.CODE, "",
                    JsonFormUtils.toList(JsonFormUtils.getChoice(context).get(choiceValue)), JsonFormUtils.toList(choiceValue), null,
                    ChildDBConstants.KEY.VACCINE_CARD).withHumanReadableValues(huValue));
            JsonFormUtils.tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), baseEvent);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            Timber.v("eventJson:%s", eventJson);
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static SpannableString daysAway(String dueDate) {
        SpannableString spannableString;
        LocalDate date1 = new LocalDate(dueDate);
        LocalDate date2 = new LocalDate();
        int diff = Days.daysBetween(date1, date2).getDays();
        if (diff <= 0) {
            String str = Math.abs(diff) + " days away";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(ChwApplication.getInstance().getContext().getColorResource(R.color.grey)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            String str = diff + " days overdue";
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(ChwApplication.getInstance().getContext().getColorResource(R.color.alert_urgent_red)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public static SpannableString dueOverdueCalculation(Context context, String status, String dueDate) {
        SpannableString spannableString;
        Date date = org.smartregister.family.util.Utils.dobStringToDate(dueDate);
        if (status.equalsIgnoreCase(ImmunizationState.DUE.name())) {

            String str = context.getResources().getString(R.string.due) + "" + dd_MMM_yyyy.format(date);
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(ChwApplication.getInstance().getContext().getColorResource(R.color.grey)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            String str = context.getResources().getString(R.string.overdue) + "" + dd_MMM_yyyy.format(date);
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(ChwApplication.getInstance().getContext().getColorResource(R.color.alert_urgent_red)), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public static ImmunizationState getDueStatus(String dueDate) {
        LocalDate date1 = new LocalDate(dueDate);
        LocalDate date2 = new LocalDate();
        int diff = Days.daysBetween(date1, date2).getDays();
        return diff <= 0 ? ImmunizationState.UPCOMING : ImmunizationState.OVERDUE;
    }


    public static void addToHomeVisitTable(String baseEntityID, List<org.smartregister.domain.db.Obs> observations) {
        HomeVisit newHomeVisit = new HomeVisit(null, baseEntityID, HomeVisitRepository.EVENT_TYPE, new Date(), "", "", "", 0l, "", "", new Date());
        try {
            for (org.smartregister.domain.db.Obs obs : observations) {
                if (obs.getFormSubmissionField().equalsIgnoreCase("singleVaccine")) {
                    newHomeVisit.setSingleVaccinesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("groupVaccine")) {
                    newHomeVisit.setVaccineGroupsGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("vaccineNotGiven")) {
                    newHomeVisit.setVaccineNotGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("service")) {
                    newHomeVisit.setServicesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("serviceNotGiven")) {
                    newHomeVisit.setServiceNotGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("birth_certificate")) {
                    try {
                        newHomeVisit.setBirthCertificationState(new JSONObject((String) obs.getValue()));
                    } catch (Exception e) {
                        // e.printStackTrace();
                        //previous support
                        newHomeVisit.setBirthCertificationState(new JSONObject());
                    }
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase("illness_information")) {
                    newHomeVisit.setIllness_information(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(ChildDBConstants.KEY.LAST_HOME_VISIT)) {
                    try {
                        newHomeVisit.setDate(new Date(Long.parseLong((String) obs.getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        newHomeVisit.setDate(new Date());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        newHomeVisit.setFormfields(new HashMap<String, String>());
        ChwApplication.homeVisitRepository().add(newHomeVisit);
    }
    public static void addToChildTable(String baseEntityID, List<org.smartregister.domain.db.Obs> observations){
        String value ="";
        for (org.smartregister.domain.db.Obs obs : observations)
            if (obs.getFormSubmissionField().equalsIgnoreCase(ChildDBConstants.KEY.VACCINE_CARD)) {
                List<Object> hu = obs.getHumanReadableValues();
                for (Object object : hu) {
                    value = (String) object;
                    break;
                }

            }
        if(!TextUtils.isEmpty(value)){
            AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.CHILD);
            ContentValues values = new ContentValues();
            values.put(ChildDBConstants.KEY.VACCINE_CARD, value);
            commonsRepository.update(Constants.TABLE_NAME.CHILD, values, baseEntityID);
        }

    }


    public static String fixVaccineCasing(String display) {
        display = display.toUpperCase();
        if (display.toLowerCase().contains("rota") || display.toLowerCase().contains("penta") || display.toLowerCase().contains("yellow fever")) {
            display = capitalize(display.toLowerCase());
        }
        return display;
    }
}
