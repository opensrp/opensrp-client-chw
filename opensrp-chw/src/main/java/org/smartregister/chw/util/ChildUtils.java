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
import com.google.gson.reflect.TypeToken;

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
import org.smartregister.chw.repository.HomeVisitServiceRepository;
import org.smartregister.chw.rule.HomeAlertRule;
import org.smartregister.chw.rule.ImmunizationExpiredRule;
import org.smartregister.chw.rule.ServiceRule;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
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
import static org.smartregister.chw.util.JsonFormUtils.getValue;
import static org.smartregister.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.chw.util.Utils.dd_MMM_yyyy;

public class ChildUtils {

    private static final String[] firstSecondNumber = {"Zero", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th"};

    public static Gson gsonConverter;

    private static Flavor childUtilsFlv = new ChildUtilsFlv();

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

    public static String getImmunizationExpired(String dateOfBirth, String vaccineName) {
        //String dob = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        ImmunizationExpiredRule immunizationExpiredRule = new ImmunizationExpiredRule(dateOfBirth, vaccineName);
        ChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(immunizationExpiredRule, Constants.RULE_FILE.IMMUNIZATION_EXPIRED);
        return immunizationExpiredRule.getButtonStatus();
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

    /**
     * Based on received vaccine list it'll return the fully immunized year.
     * Firstly it'll check with 2years vaccine list if it's match then return 2 year fully immunized.
     * Else it'll check  with 1year vaccine list otherwise it'll return empty string means not fully immunized.
     *
     * @param vaccineGiven
     * @return
     */
    public static String isFullyImmunized(List<String> vaccineGiven) {
        List<String> twoYrVac = Arrays.asList(childUtilsFlv.getTwoYearVaccines());
        if (vaccineGiven.containsAll(twoYrVac)) {
            return "2";
        }

        List<String> oneYrVac = Arrays.asList(childUtilsFlv.getOneYearVaccines());
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
        ArrayList<String> columnList = new ArrayList<>();

        columnList.add(tableName + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + ChildDBConstants.KEY.FAMILY_FIRST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + ChildDBConstants.KEY.FAMILY_LAST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME + " as " + ChildDBConstants.KEY.FAMILY_MIDDLE_NAME);
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
        columnList.addAll(childUtilsFlv.mainColumns(tableName, familyTable, familyMemberTable));

        return columnList.toArray(new String[columnList.size()]);


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

    public static void updateHomeVisitAsEvent(String entityId, String eventType, String entityType, Map<String, JSONObject> fieldObjects, String visitStatus, String value, String homeVisitId) {
        try {
            long startTime = System.currentTimeMillis();
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());

            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withValue(homeVisitId)
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            event.addObs((new Obs()).withFormSubmissionField(visitStatus).withValue(value).withFieldCode(visitStatus).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject singleVaccineObject = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE).withValue(singleVaccineObject == null ? "" : singleVaccineObject.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject vaccineGroupObject = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE).withValue(vaccineGroupObject == null ? "" : vaccineGroupObject.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject vaccineNotGiven = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN).withValue(vaccineNotGiven == null ? "" : vaccineNotGiven.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject service = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE).withValue(service == null ? "" : service.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject serviceNotGiven = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN).withValue(serviceNotGiven == null ? "" : serviceNotGiven.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject birthCert = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT).withValue(birthCert == null ? "" : birthCert.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject illnessJson = fieldObjects.get(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS);
            event.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS).withValue(illnessJson == null ? "" : illnessJson.toString())
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), event);

            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
            Log.d("TAKING_TIME","updateHomeVisitAsEvent6:"+(System.currentTimeMillis() - startTime));

            //update details

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void updateVaccineCardAsEvent(Context context, String entityId, String choiceValue) {
        try {
            long startTime = System.currentTimeMillis();
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

            baseEvent.addObs(new Obs("concept", "text", Constants.FORM_CONSTANTS.VACCINE_CARD.CODE, "",
                    JsonFormUtils.toList(JsonFormUtils.getChoice(context).get(choiceValue)), JsonFormUtils.toList(choiceValue), null,
                    ChildDBConstants.KEY.VACCINE_CARD).withHumanReadableValues(huValue));
            tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), baseEvent);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(entityId, eventJson);
//            long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
//            Date lastSyncDate = new Date(lastSyncTimeStamp);
//            Log.d("TAKING_TIME","updateVaccineCardAsEvent1:"+(System.currentTimeMillis() - startTime));
//            ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
//            Log.d("TAKING_TIME","updateVaccineCardAsEvent2:"+(System.currentTimeMillis() - startTime));
//            ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updateTaskAsEvent(String eventType, String formSubmissionField, List<Object> values, List<Object> humenread,
                                         String entityId, String choiceValue, String homeVisitId) {
        try {
            long startTime = System.currentTimeMillis();
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEntityType(HomeVisitServiceRepository.HOME_VISIT_SERVICE_TABLE_NAME)
                    .withEventType(eventType)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());
            List<Object> huValue = new ArrayList<>();
            huValue.add(choiceValue);

            baseEvent.addObs(new Obs("concept", "text", Constants.FORM_CONSTANTS.MUAC.CODE, "",
                    values, humenread, null, formSubmissionField).withHumanReadableValues(huValue));
            baseEvent.addObs((new Obs()).withFormSubmissionField(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withValue(homeVisitId)
                    .withFieldCode(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            tagSyncMetadata(ChwApplication.getInstance().getContext().allSharedPreferences(), baseEvent);
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(entityId, eventJson);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updateECDTaskAsEvent(String homeVisitId, String entityId, String jsonString) {
        try {
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = JsonFormUtils.getECDEvent(jsonString, homeVisitId, entityId);
            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                syncHelper.addEvent(entityId, eventJson);
//                long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
//                Date lastSyncDate = new Date(lastSyncTimeStamp);
//                ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
//                ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void processClientProcessInBackground(){
        long lastSyncTimeStamp = ChwApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        try {
            ChwApplication.getClientProcessor(ChwApplication.getInstance().getContext().applicationContext()).processClient(FamilyLibrary.getInstance().getEcSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChwApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

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


    public static void addToHomeVisitTable(String baseEntityID, String formSubmissionId, List<org.smartregister.domain.db.Obs> observations) {
        HomeVisit newHomeVisit = new HomeVisit(null, baseEntityID, HomeVisitRepository.EVENT_TYPE, new Date(), "", "", "", 0l, "", "", new Date());
        try {
            for (org.smartregister.domain.db.Obs obs : observations) {
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE)) {
                    newHomeVisit.setSingleVaccinesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE)) {
                    newHomeVisit.setVaccineGroupsGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN)) {
                    newHomeVisit.setVaccineNotGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE)) {
                    newHomeVisit.setServicesGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN)) {
                    newHomeVisit.setServiceNotGiven(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT)) {
                    try {
                        newHomeVisit.setBirthCertificationState(new JSONObject((String) obs.getValue()));
                    } catch (Exception e) {
                        // e.printStackTrace();
                        //previous support
                        newHomeVisit.setBirthCertificationState(new JSONObject());
                    }
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS)) {
                    newHomeVisit.setIllness_information(new JSONObject((String) obs.getValue()));
                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.LAST_HOME_VISIT)) {
                    try {
                        newHomeVisit.setDate(new Date(Long.parseLong((String) obs.getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        newHomeVisit.setDate(new Date());
                    }

                }
                if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID)) {
                    newHomeVisit.setHomeVisitId((String) obs.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        newHomeVisit.setFormSubmissionId(formSubmissionId);
        newHomeVisit.setFormfields(new HashMap<String, String>());
        ChwApplication.homeVisitRepository().add(newHomeVisit);
    }

    public static void addToHomeVisitService(String eventType, List<org.smartregister.domain.db.Obs> observations, Date evenDate, String details) {
        HomeVisitServiceDataModel homeVisitServiceDataModel = new HomeVisitServiceDataModel();
        for (org.smartregister.domain.db.Obs obs : observations) {
            if (obs.getFormSubmissionField().equalsIgnoreCase(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID)) {
                homeVisitServiceDataModel.setHomeVisitId((String) obs.getValue());
            }
        }
        homeVisitServiceDataModel.setEventType(eventType);
        homeVisitServiceDataModel.setHomeVisitDate(evenDate);
        homeVisitServiceDataModel.setHomeVisitDetails(details);
        ChwApplication.getHomeVisitServiceRepository().add(homeVisitServiceDataModel);
    }

    public static ServiceTask createServiceTaskFromEvent(String taskType, String details, String title, String formSubmissionId) {
        ServiceTask serviceTask = new ServiceTask();
        org.smartregister.domain.db.Event event = ChildUtils.gsonConverter.fromJson(details, new TypeToken<org.smartregister.domain.db.Event>() {
        }.getType());
        List<org.smartregister.domain.db.Obs> observations = event.getObs();
        for (org.smartregister.domain.db.Obs obs : observations) {
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

    public static ServiceTask createECDTaskFromEvent(Context context, String taskType, String details, String title) {
        ServiceTask serviceTask = new ServiceTask();
        org.smartregister.domain.db.Event event = ChildUtils.gsonConverter.fromJson(details, new TypeToken<org.smartregister.domain.db.Event>() {
        }.getType());
        List<org.smartregister.domain.db.Obs> observations = event.getObs();
        String label = "";
        for (org.smartregister.domain.db.Obs obs : observations) {
            if (obs.getFormSubmissionField().equalsIgnoreCase("develop_warning_signs")) {
                List<Object> hu = obs.getHumanReadableValues();
                String value = "";
                for (Object object : hu) {
                    value = (String) object;
                }
                label = context.getString(R.string.dev_warning_sign) + value;
            }
            if (obs.getFormSubmissionField().equalsIgnoreCase("stim_skills")) {
                List<Object> hu = obs.getHumanReadableValues();
                String value = "";
                for (Object object : hu) {
                    value = (String) object;
                }
                label = label + "\n" + context.getString(R.string.care_stim_skill) + value;
            }
            if (obs.getFormSubmissionField().equalsIgnoreCase("early_learning")) {
                List<Object> hu = obs.getHumanReadableValues();
                String value = "";
                for (Object object : hu) {
                    value = (String) object;
                }
                label = label + "\n" + context.getString(R.string.early_learning) + value;
            }
        }
        serviceTask.setTaskLabel(label);
        serviceTask.setTaskTitle(title);
        serviceTask.setTaskType(taskType);
        return serviceTask;

    }

    public static ServiceTask createECDFromJson(Context context, String json) {
        ServiceTask serviceTask = new ServiceTask();
        try {

            JSONObject jsonObject = new JSONObject(json);

            String value1 = getValue(jsonObject, "develop_warning_signs");
            String value2 = getValue(jsonObject, "stim_skills");
            String value3 = getValue(jsonObject, "early_learning");
            String yesVale = context.getString(R.string.yes);
            String noValue = context.getString(R.string.no);
            if (!TextUtils.isEmpty(value3) && (value3.equalsIgnoreCase(yesVale) || value3.equalsIgnoreCase(noValue))) {
                serviceTask.setTaskLabel(context.getString(R.string.dev_warning_sign) + value1 + "\n" + context.getString(R.string.care_stim_skill) + value2
                        + "\n" + context.getString(R.string.early_learning) + value3);
            } else {
                serviceTask.setTaskLabel(context.getString(R.string.dev_warning_sign) + value1 + "\n" + context.getString(R.string.care_stim_skill) + value2
                );
            }

            serviceTask.setGreen(isComplete(context, value1, value2));
            serviceTask.setTaskTitle(context.getString(R.string.ecd_title));
            serviceTask.setTaskJson(jsonObject);
            serviceTask.setTaskType(TaskServiceCalculate.TASK_TYPE.ECD.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceTask;
    }

    private static boolean isComplete(Context context, String value1, String value2){
        String yesVale = context.getString(R.string.yes);
        String noValue = context.getString(R.string.no);
        return value1.equalsIgnoreCase(noValue) && value2.equalsIgnoreCase(yesVale);
    }

    public static void addToChildTable(String baseEntityID, List<org.smartregister.domain.db.Obs> observations) {
        String value = "";
        for (org.smartregister.domain.db.Obs obs : observations)
            if (obs.getFormSubmissionField().equalsIgnoreCase(ChildDBConstants.KEY.VACCINE_CARD)) {
                List<Object> hu = obs.getHumanReadableValues();
                for (Object object : hu) {
                    value = (String) object;
                }

            }
        if (!TextUtils.isEmpty(value)) {
            AllCommonsRepository commonsRepository = ChwApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.CHILD);
            ContentValues values = new ContentValues();
            values.put(ChildDBConstants.KEY.VACCINE_CARD, value);
            commonsRepository.update(Constants.TABLE_NAME.CHILD, values, baseEntityID);
        }

    }

    /**
     * This method return the vaccine name first character as capital for selected vaccines
     * other's return as capital form like : input opv 1 output OPV 1,rota 1 as Rota 1,mena as MenA
     *
     * @param "opv 1","rota 1","mena"
     * @return "OPV 1","Rota 1","MenA"
     */

    public static String fixVaccineCasing(String display) {
        display = display.toUpperCase();
        if (display.toLowerCase().contains("mena")) {
            return "MenA";
        }
        if (display.toLowerCase().contains("rota")
                || display.toLowerCase().contains("penta")
                || display.toLowerCase().contains("yellow fever")
                || display.toLowerCase().contains("rubella")) {
            display = capitalize(display.toLowerCase());
        }

        return display;
    }

    public interface Flavor {
        ArrayList<String> mainColumns(String tableName, String familyTable, String familyMemberTable);

        String[] getOneYearVaccines();

        String[] getTwoYearVaccines();
    }
}
