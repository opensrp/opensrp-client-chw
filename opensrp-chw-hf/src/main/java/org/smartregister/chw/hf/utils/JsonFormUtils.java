package org.smartregister.chw.hf.utils;

import android.content.Context;
import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.repository.HfChwRepository;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import timber.log.Timber;

/**
 * Created by keyman on 13/11/2018.
 */
public class JsonFormUtils extends CoreJsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";


    public static Pair<Client, Event> processChildRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }
            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();
            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }
            lastInteractedWith(fields);
            dobUnknownUpdateFromAge(fields);
            processChildEnrollment(jsonForm, fields);

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, getString(jsonForm, ENCOUNTER_TYPE), CoreConstants.TABLE_NAME.CHILD);
            tagSyncMetadata(allSharedPreferences, baseEvent);

            if (baseClient != null || baseEvent != null) {
                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            JSONObject lookUpJSONObject = getJSONObject(getJSONObject(jsonForm, METADATA), "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }
            if ("family".equals(lookUpEntityId) && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client ss = new Client(lookUpBaseEntityId);
                Context context = HealthFacilityApplication.getInstance().getContext().applicationContext();
                addRelationship(context, ss, baseClient);
                SQLiteDatabase db = HealthFacilityApplication.getInstance().getRepository().getReadableDatabase();
                HfChwRepository pathRepository = new HfChwRepository(context, HealthFacilityApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientjson = eventClientRepository.getClient(db, lookUpBaseEntityId);
                baseClient.setAddresses(getAddressFromClientJson(clientjson));
            }

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private static void processChildEnrollment(JSONObject jsonForm, JSONArray fields) {
        try {
            JSONObject surnam_familyName_SameObject = getFieldJSONObject(fields, "surname_same_as_family_name");
            JSONArray surnam_familyName_Same_options = getJSONArray(surnam_familyName_SameObject, Constants.JSON_FORM_KEY.OPTIONS);
            JSONObject surnam_familyName_Same_option = getJSONObject(surnam_familyName_Same_options, 0);
            String surnam_familyName_SameString = surnam_familyName_Same_option != null ? surnam_familyName_Same_option.getString(VALUE) : null;

            if (StringUtils.isNotBlank(surnam_familyName_SameString) && Boolean.valueOf(surnam_familyName_SameString)) {
                String familyId = jsonForm.getJSONObject("metadata").getJSONObject("look_up").getString("value");
                CommonPersonObject familyObject = HealthFacilityApplication.getInstance().getContext().commonrepository("ec_family").findByCaseID(familyId);
                String lastname = familyObject.getColumnmaps().get(DBConstants.KEY.LAST_NAME);
                JSONObject surname_object = getFieldJSONObject(fields, "surname");
                surname_object.put(VALUE, lastname);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }
}
