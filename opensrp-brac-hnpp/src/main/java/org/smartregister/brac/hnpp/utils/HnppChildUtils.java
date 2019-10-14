package org.smartregister.brac.hnpp.utils;

import android.database.Cursor;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import timber.log.Timber;

public class HnppChildUtils extends CoreChildUtils {

    public static ArrayList<String> getAllWomenInHouseHold(String familyID){
        String query = "select first_name from ec_family_member where (gender = 'নারী' OR gender = 'F') and ((marital_status != 'অবিবাহিত' AND marital_status != 'Unmarried') and marital_status IS NOT NULL) and relational_id = '"+familyID+"'";
        Cursor cursor = null;
        ArrayList<String> womenList = new ArrayList<>();
        try {
            cursor = CoreChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, new String[]{});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                womenList.add(name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return womenList;
    }
    public static String getModuleId(String familyId){
        String query = "select module_id from ec_family where base_entity_id = '"+familyId+"'";
        Cursor cursor = null;
        try {
            cursor = CoreChwApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, new String[]{});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                return name;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "";
    }

    public static String matchPhrase(String phrase) {
        String stringPhrase = phrase;
        if (stringPhrase == null) {
            stringPhrase = "";
        }

        // Underscore does not work well in fts search
//        if (stringPhrase.contains("_")) {
//            stringPhrase = stringPhrase.replace("_", "");
//        }
        return " MATCH '" + stringPhrase + "*' ";

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
        columnList.add(familyTable + "." + HnppConstants.KEY.CLASTER);
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
        columnList.add(tableName + "." + HnppConstants.KEY.RELATION_WITH_HOUSEHOLD);
        columnList.add(tableName + "." + HnppConstants.KEY.CHILD_MOTHER_NAME);
        columnList.add(tableName + "." + HnppConstants.KEY.CHILD_MOTHER_NAME_REGISTERED);
        columnList.add(tableName + "." + HnppConstants.KEY.BLOOD_GROUP);
        return columnList.toArray(new String[columnList.size()]);
    }

    public static void updateHomeVisitAsEvent(String entityId, String eventType, String entityType, Map<String, JSONObject> fieldObjects, String visitStatus, String value, String homeVisitId) {
        try {
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)
                    .withEventDate(new Date())
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(CoreJsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());

            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withValue(homeVisitId)
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            event.addObs((new Obs()).withFormSubmissionField(visitStatus).withValue(value).withFieldCode(visitStatus).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject singleVaccineObject = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE).withValue(singleVaccineObject == null ? "" : singleVaccineObject.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject vaccineGroupObject = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE).withValue(vaccineGroupObject == null ? "" : vaccineGroupObject.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject vaccineNotGiven = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN).withValue(vaccineNotGiven == null ? "" : vaccineNotGiven.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject service = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE).withValue(service == null ? "" : service.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject serviceNotGiven = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN).withValue(serviceNotGiven == null ? "" : serviceNotGiven.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject birthCert = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT).withValue(birthCert == null ? "" : birthCert.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            JSONObject illnessJson = fieldObjects.get(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS);
            event.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS).withValue(illnessJson == null ? "" : illnessJson.toString())
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

            CoreJsonFormUtils.tagSyncMetadata(HnppApplication.getInstance().getContext().allSharedPreferences(), event);

            JSONObject eventJson = new JSONObject(CoreJsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = HnppApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HnppApplication.getClientProcessor(HnppApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HnppApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
