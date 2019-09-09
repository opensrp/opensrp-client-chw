package org.smartregister.brac.hnpp.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.utils.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class HnppFamilyRegisterModel extends BaseFamilyRegisterModel {

    public ArrayList<SSLocationForm> getSsLocationForms() {
        return ssLocationForms;
    }

    private ArrayList<SSLocationForm> ssLocationForms = new ArrayList<>();
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        updateSSNameLocation();
        JsonFormUtils.updateFormWithSSLocation(form,getSsLocationForms());
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    @Override
    public List<FamilyEventClient> processRegistration(String jsonString) {
        List<FamilyEventClient> familyEventClientList = new ArrayList<>();
        FamilyEventClient familyEventClient = JsonFormUtils.processFamilyUpdateForm(Utils.context().allSharedPreferences(), jsonString);
        if (familyEventClient == null) {
            return familyEventClientList;
        }

//        FamilyEventClient headEventClient = JsonFormUtils.processFamilyHeadRegistrationForm(Utils.context().allSharedPreferences(), jsonString, familyEventClient.getClient().getBaseEntityId());
//        if (headEventClient == null) {
//            return familyEventClientList;
//        }

//        if (headEventClient.getClient() != null && familyEventClient.getClient() != null) {
            String headUniqueId = familyEventClient.getClient().getIdentifier(Utils.metadata().uniqueIdentifierKey);
            if (StringUtils.isNotBlank(headUniqueId)) {
                String familyUniqueId = headUniqueId + Constants.IDENTIFIER.FAMILY_SUFFIX;
                familyEventClient.getClient().addIdentifier(Utils.metadata().uniqueIdentifierKey, familyUniqueId);
            }
        //}

        // Update the family head and primary caregiver
        Client familyClient = familyEventClient.getClient();
        familyClient.addRelationship(Utils.metadata().familyRegister.familyHeadRelationKey, familyEventClient.getClient().getBaseEntityId());
        familyClient.addRelationship(Utils.metadata().familyRegister.familyCareGiverRelationKey, familyEventClient.getClient().getBaseEntityId());

        familyEventClientList.add(familyEventClient);
        //familyEventClientList.add(headEventClient);
        return familyEventClientList;
    }

    public void updateSSNameLocation(){
        ssLocationForms.addAll(SSLocationHelper.getInstance().getSsLocationForms());
    }
}
