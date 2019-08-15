package org.smartregister.chw.hf.utils;

import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import timber.log.Timber;

public class HfChildUtils extends CoreChildUtils {

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

            CoreJsonFormUtils.tagSyncMetadata(HealthFacilityApplication.getInstance().getContext().allSharedPreferences(), event);

            JSONObject eventJson = new JSONObject(CoreJsonFormUtils.gson.toJson(event));
            syncHelper.addEvent(entityId, eventJson);
            long lastSyncTimeStamp = HealthFacilityApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HealthFacilityApplication.getClientProcessor(HealthFacilityApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HealthFacilityApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
