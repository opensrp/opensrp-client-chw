package org.smartregister.brac.hnpp.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.repository.HouseholdIdRepository;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.FIELDS;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

public class HnppFamilyRegisterModel extends BaseFamilyRegisterModel {


    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        HnppJsonFormUtils.updateFormWithSSName(form,SSLocationHelper.getInstance().getSsModels());
        return HnppJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    @Override
    public List<FamilyEventClient> processRegistration(String jsonString) {
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jobkect = jsonObject.getJSONObject("step1");
            String villageIndex = jobkect.getString("village_index");
            String ssIndex = jobkect.getString("ss_index");
            SSLocations ss = SSLocationHelper.getInstance().getSsModels().get(Integer.parseInt(ssIndex)).locations.get(Integer.parseInt(villageIndex));
            JSONArray field = jobkect.getJSONArray(FIELDS);
            JSONObject villageIdObj = getFieldJSONObject(field, "village_id");
            String villageId = villageIdObj.getString(VALUE);
            try{
                String hhid = jobkect.getString( "hhid");
                HouseholdIdRepository householdIdRepo = HnppApplication.getHNPPInstance().getHouseholdIdRepository();
                householdIdRepo.close(villageId,hhid);
            }catch (Exception e){

            }
            List<FamilyEventClient> familyEventClientList = new ArrayList<>();

            if(jsonObject.has(org.smartregister.family.util.JsonFormUtils.STEP2)){
                JSONObject stepTwo = jsonObject.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP2);
                JSONArray jsonArray2 = stepTwo.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray2.length(); i++) {
                    field.put(jsonArray2.getJSONObject(i));
                }

//                processAttributesWithChoiceIDsForSave(jsonArray2);
                jsonObject.remove(org.smartregister.family.util.JsonFormUtils.STEP2);
            }

            processAttributesWithChoiceIDsForSave(field);
            FamilyEventClient familyEventClient = HnppJsonFormUtils.processFamilyUpdateForm(Utils.context().allSharedPreferences(), jsonObject.toString());
            if (familyEventClient == null) {
                return familyEventClientList;
            }

//        FamilyEventClient headEventClient = HnppJsonFormUtils.processFamilyHeadRegistrationForm(Utils.context().allSharedPreferences(), jsonString, familyEventClient.getClient().getBaseEntityId());
//        if (headEventClient == null) {
//            return familyEventClientList;
//        }

//        if (headEventClient.getClient() != null && familyEventClient.getClient() != null) {
            String headUniqueId = familyEventClient.getClient().getIdentifier(Utils.metadata().uniqueIdentifierKey);
            if (StringUtils.isNotBlank(headUniqueId)) {
                //String familyUniqueId = headUniqueId ;//+ Constants.IDENTIFIER.FAMILY_SUFFIX;
                familyEventClient.getClient().addIdentifier(Utils.metadata().uniqueIdentifierKey, headUniqueId);
            }
            //}

            // Update the family head and primary caregiver
            Client familyClient = familyEventClient.getClient();
            familyClient.addRelationship(Utils.metadata().familyRegister.familyHeadRelationKey, familyEventClient.getClient().getBaseEntityId());
            familyClient.addRelationship(Utils.metadata().familyRegister.familyCareGiverRelationKey, familyEventClient.getClient().getBaseEntityId());
            List<Address> listAddress = new ArrayList<>();
            listAddress.add(SSLocationHelper.getInstance().getSSAddress(ss));
            familyClient.setAddresses(listAddress);
            familyEventClientList.add(familyEventClient);

            //familyEventClientList.add(headEventClient);
            return familyEventClientList;

        }catch (Exception e){
            Timber.e(e);
        }
        return null;

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

}
