package org.smartregister.chw.model;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.utils.OpdUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.util.JsonFormUtils.METADATA;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION;
import static org.smartregister.util.JsonFormUtils.STEP1;

public class ChwAllClientsRegisterModel extends OpdRegisterActivityModel {

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) {
        try {
            JSONObject form = OpdUtils.getJsonFormToJsonObject(formName);
            if (form == null) {
                return null;
            }

            form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            JSONObject stepOneUniqueId = getFieldJSONObject(fields(form, STEP1), Constants.JSON_FORM_KEY.UNIQUE_ID);

            if (stepOneUniqueId != null) {
                stepOneUniqueId.remove(JsonFormUtils.VALUE);
                stepOneUniqueId.put(JsonFormUtils.VALUE, entityId + "_Family");
            }

           JSONObject stepTwoUniqueId = getFieldJSONObject( fields(form, STEP2), Constants.JSON_FORM_KEY.UNIQUE_ID);
            if (stepTwoUniqueId != null) {
                stepTwoUniqueId.remove(JsonFormUtils.VALUE);
                stepTwoUniqueId.put(JsonFormUtils.VALUE, entityId);
            }

            JsonFormUtils.addLocHierarchyQuestions(form);
            return form;

        } catch (Exception e) {
            Timber.e(e, "Error loading All Client registration form");
        }
        return null;
    }

    @Nullable
    @Override
    public List<OpdEventClient> processRegistration(String jsonString, FormTag formTag) {

        List<OpdEventClient> allClientMemberEvents = new ArrayList<>();

        FamilyEventClient locationDetailsEvent = JsonFormUtils.processFamilyUpdateForm(
                Utils.context().allSharedPreferences(), jsonString);
        if (locationDetailsEvent == null) {
            return allClientMemberEvents;
        }

        FamilyEventClient clientDetailsEvent = JsonFormUtils.processFamilyHeadRegistrationForm(
                Utils.context().allSharedPreferences(), jsonString, locationDetailsEvent.getClient().getBaseEntityId());
        if (clientDetailsEvent == null) {
            return allClientMemberEvents;
        }

        if (clientDetailsEvent.getClient() != null && locationDetailsEvent.getClient() != null) {
            String headUniqueId = clientDetailsEvent.getClient().getIdentifier(Utils.metadata().uniqueIdentifierKey);
            if (StringUtils.isNotBlank(headUniqueId)) {
                String familyUniqueId = headUniqueId + Constants.IDENTIFIER.FAMILY_SUFFIX;
                locationDetailsEvent.getClient().addIdentifier(Utils.metadata().uniqueIdentifierKey, familyUniqueId);
            }
        }

        // Update the family head and primary caregiver
        Client familyClient = locationDetailsEvent.getClient();
        familyClient.addRelationship(Utils.metadata().familyRegister.familyHeadRelationKey, clientDetailsEvent.getClient().getBaseEntityId());
        familyClient.addRelationship(Utils.metadata().familyRegister.familyCareGiverRelationKey, clientDetailsEvent.getClient().getBaseEntityId());
        clientDetailsEvent.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
        allClientMemberEvents.add(new OpdEventClient(locationDetailsEvent.getClient(), locationDetailsEvent.getEvent()));
        allClientMemberEvents.add(new OpdEventClient(clientDetailsEvent.getClient(), clientDetailsEvent.getEvent()));
        return allClientMemberEvents;
    }
}
