package org.smartregister.brac.hnpp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.location.SSModel;
import org.smartregister.brac.hnpp.repository.HnppChwRepository;
import org.smartregister.brac.hnpp.repository.HnppVisitLogRepository;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.repository.WashCheckRepository;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.LocationPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by keyman on 13/11/2018.
 */
public class HnppJsonFormUtils extends CoreJsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String SS_NAME = "ss_name";
    public static final String SIMPRINTS_ENABLE = "simprints_enable";
    public static final String VILLAGE_NAME = "village_name";
    public static final String ENCOUNTER_TYPE = "encounter_type";

    private static VisitRepository visitRepository() {
        return AncLibrary.getInstance().visitRepository();
    }
    public static Visit saveVisit(boolean editMode, String memberID, String encounterType,
                            final Map<String, String> jsonString,
                            String parentEventType) throws Exception {

        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();

        String derivedEncounterType = StringUtils.isBlank(parentEventType) ? encounterType : "";
        Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processVisitJsonForm(allSharedPreferences, memberID, derivedEncounterType, jsonString, getTableName());

        if (StringUtils.isBlank(parentEventType))
            prepareEvent(baseEvent);

        if (baseEvent != null) {
            baseEvent.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            org.smartregister.chw.anc.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);

            String visitID = JsonFormUtils.generateRandomUUIDString();

            Visit visit = NCUtils.eventToVisit(baseEvent, visitID);
            visit.setPreProcessedJson(new Gson().toJson(baseEvent));
            visit.setParentVisitID(visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate()));

            visitRepository().addVisit(visit);
            return visit;
        }
        return null;
    }
    public static String getEncounterType() {
        return org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT;
    }

    private static String getTableName() {
        return org.smartregister.chw.anc.util.Constants.TABLES.ANC_MEMBERS;
    }
    private static void prepareEvent(Event baseEvent) {
        if (baseEvent != null) {
            // add anc date obs and last
            List<Object> list = new ArrayList<>();
            list.add(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            baseEvent.addObs(new Obs("concept", "text", "anc_visit_date", "",
                    list, new ArrayList<>(), null, "anc_visit_date"));
        }
    }
    public static JSONObject updateFormWithModuleId(JSONObject form,String moduleId, String familyBaseEntityId) throws JSONException {
        JSONArray field = fields(form, STEP1);
        JSONObject fingerPrint = getFieldJSONObject(field, "finger_print");
        fingerPrint.put("project_id", HnppConstants.getSimPrintsProjectId());
        fingerPrint.put("user_id",CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM());
        fingerPrint.put("module_id",moduleId);
        form.put("family_name", HnppDBUtils.getFamilyName(familyBaseEntityId));

        String entity_id = form.getString("entity_id");
        try {

            if(StringUtils.isEmpty(entity_id)){
                ArrayList<String> womenList = HnppDBUtils.getAllWomenInHouseHold(familyBaseEntityId);
                HnppJsonFormUtils.updateFormWithMotherName(form,womenList);
            }else{
                ArrayList<String> womenList = HnppDBUtils.getAllWomenInHouseHold(entity_id,familyBaseEntityId);
                HnppJsonFormUtils.updateFormWithMotherName(form,womenList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        form.put("relational_id", familyBaseEntityId);

        return form;
    }

    public static boolean saveVisitLogEvent(String jsonString, String baseEntityId) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!registrationFormParams.getLeft()) {
                return false;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();

            JSONArray fields = registrationFormParams.getRight();
            JSONObject metadata = getJSONObject(jsonForm, METADATA);
            String entityId = getString(jsonForm, ENTITY_ID);
            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            String entitytypeName = HnppVisitLogRepository.VISIT_LOG_TABLE_NAME;

            FormTag formTag = new FormTag();
            formTag.providerId = HnppApplication.getHNPPInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            formTag.appVersion = BuildConfig.VERSION_CODE;
            formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, entitytypeName);
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();


            tagSyncMetadata(org.smartregister.family.util.Utils.context().allSharedPreferences(), baseEvent);// tag docs

            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(baseEntityId, eventJson);
            long lastSyncTimeStamp = HnppApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HnppApplication.getClientProcessor(HnppApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HnppApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
//            HnppApplication.getHNPPInstance().getHnppVisitLogRepository().add(createNewVisitLog(baseEntityId,));
            return true;
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static VisitLog createNewVisitLog(String baseEntityId,String event_type,String visit_type,String jsonString) {
        VisitLog visitLog = new VisitLog();
        visitLog.setVisitId(generateRandomUUIDString());
        visitLog.setVisitType(visit_type);
        visitLog.setBaseEntityId(baseEntityId);
        visitLog.setVisitDate(new Date().getTime());
        visitLog.setEventType(event_type);
        visitLog.setVisitJson(jsonString);
        return visitLog;
    }

    public static JSONObject updateFormWithMemberId(JSONObject form,String houseHoldId, String familyBaseEntityId) throws JSONException {
        JSONArray field = fields(form, STEP1);
        JSONObject memberId = getFieldJSONObject(field, "unique_id");
        if(!TextUtils.isEmpty(houseHoldId)){
            houseHoldId = houseHoldId.replace(Constants.IDENTIFIER.FAMILY_SUFFIX,"")
                    .replace(HnppConstants.IDENTIFIER.FAMILY_TEXT,"");
        }

        int memberCount = HnppApplication.ancRegisterRepository().getMemberCount(familyBaseEntityId);
        memberId.put(org.smartregister.family.util.JsonFormUtils.VALUE, houseHoldId+memberCountWithZero(memberCount+1));
        return form;
    }
    public static JSONObject updateFormWithSimPrintsEnable(JSONObject form) throws Exception{

        boolean simPrintsEnable = true;
        ArrayList<SSModel> ssLocationForms = SSLocationHelper.getInstance().getSsModels();
        if(ssLocationForms.size() > 0){
            simPrintsEnable = ssLocationForms.get(0).simprints_enable;
        }
        JSONArray field = fields(form, STEP1);
        JSONObject simprintObj = getFieldJSONObject(field, SIMPRINTS_ENABLE);
        simprintObj.put(org.smartregister.family.util.JsonFormUtils.VALUE,simPrintsEnable);

        return form;


    }
    public static JSONObject updateChildFormWithMetaData(JSONObject form,String houseHoldId, String familyBaseEntityId) throws JSONException {

        JSONObject lookUpJSONObject = getJSONObject(getJSONObject(form, METADATA), "look_up");
        lookUpJSONObject.put("entity_id","family");
        lookUpJSONObject.put("value",familyBaseEntityId);
        form.put("relational_id", familyBaseEntityId);
        JSONArray field = fields(form, STEP1);
        JSONObject houseHoldIdObject = getFieldJSONObject(field, "house_hold_id");
        houseHoldIdObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, houseHoldId);
        return form;
    }
    public static JSONObject updateFormWithSSName(JSONObject form, ArrayList<SSModel> ssLocationForms) throws Exception{

        JSONArray jsonArray = new JSONArray();
        for(SSModel ssLocationForm : ssLocationForms){
            jsonArray.put(ssLocationForm.username);
        }
        JSONArray field = fields(form, STEP1);
        JSONObject spinner = getFieldJSONObject(field, SS_NAME);

        spinner.put(org.smartregister.family.util.JsonFormUtils.VALUES,jsonArray);
        return form;


    }
    public static JSONObject updateFormWithVillageName(JSONObject form, String ssName , String villageName) throws Exception{

        JSONArray jsonArray = new JSONArray();
        ArrayList<SSLocations> ssLocations = new ArrayList<>();

        ArrayList<SSModel> modelList = SSLocationHelper.getInstance().getSsModels();
        int ssIndex = -1,villageIndex = -1;
        for(int i = 0; i< modelList.size(); i++){
            SSModel ssModel = modelList.get(i);
            if(ssModel.username.equalsIgnoreCase(ssName)){
                ssLocations = ssModel.locations;
                ssIndex = i;
                break;
            }
        }

        for(int i = 0; i< ssLocations.size(); i++){
            SSLocations ssLocations1 = ssLocations.get(i);
            if(ssLocations1.village.name.equalsIgnoreCase(villageName)){
                villageIndex = i;
            }
            jsonArray.put(ssLocations1.village.name);
        }
        JSONObject step1 = form.getJSONObject(STEP1);
        step1.put("ss_index", ssIndex);
        step1.put("village_index", villageIndex);
        JSONArray field = fields(form, STEP1);
        JSONObject spinner = getFieldJSONObject(field, VILLAGE_NAME);
        getFieldJSONObject(field, VILLAGE_NAME);
        spinner.put(org.smartregister.family.util.JsonFormUtils.VALUES,jsonArray);
        return form;


    }
    public static JSONObject updateFormWithMotherName(JSONObject form , ArrayList<String> motherNameList) throws Exception{

        JSONArray jsonArray = new JSONArray();
        for(String name : motherNameList){
            jsonArray.put(name);
        }
        jsonArray.put("মাতা রেজিস্টার্ড নয়");
        JSONArray field = fields(form, STEP1);
        JSONObject spinner = getFieldJSONObject(field, "mother_name");

        spinner.put(org.smartregister.family.util.JsonFormUtils.VALUES,jsonArray);
        return form;


    }

    public static JSONObject getAutoPopulatedJsonEditFormString(String formName, Context context, CommonPersonObjectClient client, String eventType) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            Timber.d("Form is %s", form.toString());
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject);

                }

                if(form.has(org.smartregister.family.util.JsonFormUtils.STEP2)) {
                    JSONObject stepTwo = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP2);

                    JSONArray jsonArray2 = stepTwo.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject = jsonArray2.getJSONObject(i);

                        processPopulatableFields(client, jsonObject);

                    }
                }


                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }
    protected static void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {

        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {

            case "firstname":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),DBConstants.KEY.FIRST_NAME, false));

                break;
            case "village_name":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),DBConstants.KEY.VILLAGE_TOWN, false));

                break;
            case Constants.JSON_FORM_KEY.DOB:
                getDob(client,jsonObject);
                break;
            case Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
                break;
            case "ss_name":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),HnppConstants.KEY.SS_NAME, false));

                break;


            case "first_name":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),DBConstants.KEY.FIRST_NAME, false));
            break;
            case "contact_phone_number":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),DBConstants.KEY.PHONE_NUMBER, false));

             break;
            case "mother_guardian_first_name_english":
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                        org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),HnppConstants.KEY.CHILD_MOTHER_NAME, false));

                break;
            case "mother_name":
                String motherNameAlter = Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.CHILD_MOTHER_NAME_REGISTERED, true);
                if(!TextUtils.isEmpty(motherNameAlter) && motherNameAlter.equalsIgnoreCase("মাতা রেজিস্টার্ড নয়")){
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,motherNameAlter);
                }else{
                    String motherEntityId = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.MOTHER_ENTITY_ID, true);
                    String relationId = Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, true);
                    String motherName = Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.CHILD_MOTHER_NAME, true);

                    motherName = HnppDBUtils.getMotherName(motherEntityId,relationId,motherName);
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,motherName);
                }

                break;

            case "sex":
                if(jsonObject.has("openmrs_choice_ids")&&jsonObject.getJSONObject("openmrs_choice_ids").length()>0){
                    String value = processValueWithChoiceIdsForEdit(jsonObject,org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),
                            DBConstants.KEY.GENDER, false));
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,value);
                }else{
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                            org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),DBConstants.KEY.GENDER, false));
                }


                break;
            case "id_avail":
                if(jsonObject.has("options")){
                    String value = processValueWithChoiceIdsForEdit(jsonObject,org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),
                            "id_avail", false));
                    if(StringUtils.isEmpty(value)){
                        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,new JSONArray());
                    }else{
                        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,new JSONArray(value));
                    }


                }
                break;
            default:
                if(jsonObject.has("openmrs_choice_ids")&&jsonObject.getJSONObject("openmrs_choice_ids").length()>0){
                    String value = processValueWithChoiceIdsForEdit(jsonObject,org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),
                            jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,value);
                }else{
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                            org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(),
                                    jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));
                }



                break;
        }
    }
    private static void getDob(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {
        String dobString = org.smartregister.chw.core.utils.Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = org.smartregister.chw.core.utils.Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, dd_MM_yyyy.format(dob));
            }
        }
    }
    public static FamilyEventClient processFamilyMemberForm(AllSharedPreferences allSharedPreferences, String jsonString, String familyBaseEntityId, String encounterType) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!(Boolean)registrationFormParams.getLeft()) {
                return null;
            } else {
                JSONObject jsonForm = (JSONObject)registrationFormParams.getMiddle();
                JSONArray fields = (JSONArray)registrationFormParams.getRight();
                String familyId = getString(jsonForm, "relational_id");
                String entityId = getString(jsonForm, "entity_id");
                if (StringUtils.isBlank(entityId)) {
                    entityId = generateRandomUUIDString();
                }

                lastInteractedWith(fields);
                dobEstimatedUpdateFromAge(fields);
                String motherEntityId = updateMotherName(fields,familyId);

                FormTag formTag = formTag(allSharedPreferences);
                formTag.appVersionName = BuildConfig.VERSION_NAME;
                Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                if (baseClient != null && !baseClient.getBaseEntityId().equals(familyBaseEntityId)) {
                    baseClient.addRelationship(Utils.metadata().familyMemberRegister.familyRelationKey, familyBaseEntityId);
                }

                Context context = HnppApplication.getInstance().getContext().applicationContext();
                addRelationship(context, motherEntityId,familyId, baseClient);
                Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, "metadata"), formTag, entityId, encounterType, Utils.metadata().familyMemberRegister.tableName);
                tagSyncMetadata(allSharedPreferences, baseEvent);

                String entity_id = baseClient.getBaseEntityId();
                updateFormSubmissionID(encounterType,entity_id,baseEvent);
                return new FamilyEventClient(baseClient, baseEvent);
            }
        } catch (Exception var10) {
            Timber.e(var10);
            return null;
        }
    }
//    public static FamilyEventClient processFamilyUpdateForm(AllSharedPreferences allSharedPreferences, String jsonString) {
//        try {
//            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
//            if (!(Boolean)registrationFormParams.getLeft()) {
//                return null;
//            } else {
//                JSONObject jsonForm = (JSONObject)registrationFormParams.getMiddle();
//                JSONArray fields = (JSONArray)registrationFormParams.getRight();
//                String entityId = getString(jsonForm, "entity_id");
//                if (StringUtils.isBlank(entityId)) {
//                    entityId = generateRandomUUIDString();
//                }
//
//                lastInteractedWith(fields);
//                processAttributesWithChoiceIDsForSave(fields);
//                Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
//                baseClient.setLastName("Family");
//                baseClient.setBirthdate(new Date(0L));
//                baseClient.setGender("Male");
//                Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, "metadata"), formTag(allSharedPreferences), entityId, Utils.metadata().familyRegister.registerEventType, Utils.metadata().familyRegister.tableName);
//                tagSyncMetadata(allSharedPreferences, baseEvent);
//                return new FamilyEventClient(baseClient, baseEvent);
//            }
//        } catch (Exception var8) {
//            Timber.e(var8);
//            return null;
//        }
//    }
    protected static void dobEstimatedUpdateFromAge(JSONArray fields) {
        try {
            JSONObject dobUnknownObject = getFieldJSONObject(fields, "is_birthday_known");
            String dobUnKnownString = dobUnknownObject != null ? dobUnknownObject.getString("value") : null;
            if (StringUtils.isNotBlank(dobUnKnownString) && dobUnKnownString.equalsIgnoreCase("না")) {
                String ageString = getFieldValue(fields, "estimated_age");
                if (StringUtils.isNotBlank(ageString) && NumberUtils.isNumber(ageString)) {
                    int age = Integer.valueOf(ageString);
                    JSONObject dobJSONObject = getFieldJSONObject(fields, "dob");
                    dobJSONObject.put("value", Utils.getDob(age));
                }
            }
        } catch (JSONException var9) {
            Timber.e(var9);
        }
        processAttributesWithChoiceIDsForSave(fields);
    }
    private static JSONArray processAttributesWithChoiceIDsForSave(JSONArray fields) {
        for (int i = 0; i < fields.length(); i++) {
            try {
                JSONObject fieldObject = fields.getJSONObject(i);
//                if(fieldObject.has("openmrs_entity")){
//                    if(fieldObject.getString("openmrs_entity").equalsIgnoreCase("person_attribute")){
                if (fieldObject.has("openmrs_choice_ids")&&fieldObject.getJSONObject("openmrs_choice_ids").length()>0) {
                    if (fieldObject.has("value")) {
                        String valueEntered = fieldObject.getString("value");
                        fieldObject.put("value", fieldObject.getJSONObject("openmrs_choice_ids").get(valueEntered));
                    }
                }
//                    }
//                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
        return fields;
    }

    private static String processValueWithChoiceIdsForEdit(JSONObject jsonObject, String value) {
        try {
            //spinner
            if (jsonObject.has("openmrs_choice_ids")) {
                JSONObject choiceObject = jsonObject.getJSONObject("openmrs_choice_ids");

                for (int i = 0; i < choiceObject.names().length(); i++) {
                    if (value.equalsIgnoreCase(choiceObject.getString(choiceObject.names().getString(i)))) {
                        value = choiceObject.names().getString(i);
                        return value;
                    }
                }
            }else if (jsonObject.has("options")) {
                JSONArray option_array = jsonObject.getJSONArray("options");
                for (int i = 0; i < option_array.length(); i++) {
                    JSONObject option = option_array.getJSONObject(i);
                    if (value.contains(option.optString("key"))) {
                        option.put("value", "true");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static String memberCountWithZero(int count){
        return count<10 ? "0"+count : String.valueOf(count);
    }


    public static JSONObject getFormAsJson(JSONObject form,
                                                      String formName, String id,
                                                      String currentLocationId) throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        if (Utils.metadata().familyRegister.formName.equals(formName) || Utils.metadata().familyMemberRegister.formName.equals(formName)) {
            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            JSONArray field = fields(form, STEP1);
            JSONObject uniqueId = getFieldJSONObject(field, Constants.JSON_FORM_KEY.UNIQUE_ID);

            if (formName.equals(Utils.metadata().familyRegister.formName)) {
                if (uniqueId != null) {
                    uniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                   uniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, entityId);
                }

                // Inject opensrp id into the form
                field = fields(form, STEP2);
                uniqueId = getFieldJSONObject(field, Constants.JSON_FORM_KEY.UNIQUE_ID);
                if (uniqueId != null) {
                    uniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                    uniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, entityId);
                }
            } else {
                if (uniqueId != null) {
                    uniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                    uniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, entityId);
                }
            }

            org.smartregister.family.util.JsonFormUtils.addLocHierarchyQuestions(form);

        } else {
            Timber.w("Unsupported form requested for launch " + formName);
        }
        Timber.d("form is " + form.toString());
        return form;
    }

    public static Pair<Client, Event> processChildRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }
            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();
            String entityId = getString(jsonForm, ENTITY_ID);
            String familyId = getString(jsonForm, "relational_id");
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }
            String motherEntityId = updateMotherName(fields,familyId);
            lastInteractedWith(fields);
            dobUnknownUpdateFromAge(fields);
            processAttributesWithChoiceIDsForSave(fields);
            FormTag formTag = formTag(allSharedPreferences);
            formTag.appVersionName = BuildConfig.VERSION_NAME;
            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields,formTag , entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag, entityId, getString(jsonForm, ENCOUNTER_TYPE), CoreConstants.TABLE_NAME.CHILD);
            tagSyncMetadata(allSharedPreferences, baseEvent);
            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            String entity_id = baseClient.getBaseEntityId();
            updateFormSubmissionID(encounterType,entity_id,baseEvent);
            JSONObject lookUpJSONObject = getJSONObject(getJSONObject(jsonForm, METADATA), "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }
            if ("family".equals(lookUpEntityId) && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Context context = HnppApplication.getInstance().getContext().applicationContext();
                addRelationship(context, motherEntityId,lookUpBaseEntityId, baseClient);
                SQLiteDatabase db = HnppApplication.getInstance().getRepository().getReadableDatabase();
                HnppChwRepository pathRepository = new HnppChwRepository(context, HnppApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientjson = eventClientRepository.getClient(db, lookUpBaseEntityId);
                baseClient.setAddresses(updateWithSSLocation(clientjson));
            }

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }
    public static void addRelationship(Context context, String motherEntityId, String familyId, Client child) {
        try {
            String relationships = AssetHandler.readFileFromAssetsFolder(FormUtils.ecClientRelationships, context);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field") && getString(rObject, "field").equals(ENTITY_ID)) {
                    if(rObject.getString("client_relationship").equalsIgnoreCase("family")){
                        child.addRelationship(rObject.getString("client_relationship"), familyId);

                    }else if(rObject.getString("client_relationship").equalsIgnoreCase("mother")){
                        child.addRelationship(rObject.getString("client_relationship"), motherEntityId);

                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
    private static String updateMotherName(JSONArray fields , String familyId) throws Exception{
        JSONObject motherObj = getFieldJSONObject(fields, HnppConstants.KEY.CHILD_MOTHER_NAME);
        JSONObject motherAlterObj = getFieldJSONObject(fields, HnppConstants.KEY.CHILD_MOTHER_NAME_REGISTERED);
        boolean isVisible = motherObj.optBoolean("is_visible",false);
        if(!isVisible){
            String motherNameSelected = motherAlterObj.optString(VALUE);
            if(!TextUtils.isEmpty(motherNameSelected) && !motherNameSelected.equalsIgnoreCase("মাতা রেজিস্টার্ড নয়")){
                motherObj.put(VALUE,motherNameSelected);
            }

        }
        String motherName = motherObj.optString(VALUE);
        if(!TextUtils.isEmpty(motherName))return HnppDBUtils.getMotherBaseEntityId(familyId,motherName);
        return "";


    }

    private static List<Address> updateWithSSLocation(JSONObject clientjson){
        try{
            String addessJson = clientjson.getString("addresses");
            JSONArray jsonArray = new JSONArray(addessJson);
            List<Address> listAddress = new ArrayList<>();
            for(int i = 0; i <jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Address address = new Gson().fromJson(jsonObject.toString(), Address.class);
                listAddress.add(address);
            }
            return listAddress;
        }catch (Exception e){

        }
        return new ArrayList<>();

    }
    public static FamilyEventClient processFamilyUpdateForm(AllSharedPreferences allSharedPreferences, String jsonString) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!(Boolean)registrationFormParams.getLeft()) {
                return null;
            } else {
                JSONObject jsonForm = (JSONObject)registrationFormParams.getMiddle();
                JSONArray fields = (JSONArray)registrationFormParams.getRight();
                String entityId = getString(jsonForm, "entity_id");
                if (StringUtils.isBlank(entityId)) {
                    entityId = generateRandomUUIDString();
                }

                lastInteractedWith(fields);
                dobUnknownUpdateFromAge(fields);
                FormTag formTag = formTag(allSharedPreferences);
                formTag.appVersionName = BuildConfig.VERSION_NAME;
                Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                baseClient.setLastName("Family");
                baseClient.setBirthdate(new Date(0L));
                baseClient.setGender("Male");
                Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, "metadata"), formTag, entityId, Utils.metadata().familyRegister.registerEventType, Utils.metadata().familyRegister.tableName);
                tagSyncMetadata(allSharedPreferences, baseEvent);

                String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
                String entity_id = baseClient.getBaseEntityId();
                updateFormSubmissionID(encounterType,entity_id,baseEvent);
                return new FamilyEventClient(baseClient, baseEvent);
            }
        } catch (Exception var8) {
            Timber.e(var8);
            return null;
        }
    }

    public static void updateFormSubmissionID(String encounterType, String entity_id, Event baseEvent) throws JSONException{
        String formSubmissionID = "";

        EventClientRepository eventClientRepository = HnppApplication.getHNPPInstance().getEventClientRepository();
        JSONObject evenjsonobject = eventClientRepository.getEventsByBaseEntityIdAndEventType(entity_id, encounterType);
        if (evenjsonobject == null) {
            if (encounterType.contains("Update")) {
                evenjsonobject = eventClientRepository.getEventsByBaseEntityIdAndEventType(entity_id, encounterType.replace("Update", "").trim());
            }
        }
        if (evenjsonobject != null) {
            formSubmissionID = evenjsonobject.getString("formSubmissionId");
        }
        if (!isBlank(formSubmissionID)) {
            baseEvent.setFormSubmissionId(formSubmissionID);
        }
    }
}
