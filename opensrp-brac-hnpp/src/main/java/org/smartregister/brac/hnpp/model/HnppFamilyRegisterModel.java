package org.smartregister.brac.hnpp.model;

import org.apache.commons.lang3.StringUtils;
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
            JSONObject jobkect = new JSONObject(jsonString).getJSONObject("step1");
            String villageIndex = jobkect.getString("village_index");
            String ssIndex = jobkect.getString("ss_index");
            String hhid = jobkect.getString("hhid");
            SSLocations ss = SSLocationHelper.getInstance().getSsModels().get(Integer.parseInt(ssIndex)).locations.get(Integer.parseInt(villageIndex));
            String villageId = ss.village.id+"";
            HouseholdIdRepository householdIdRepo = HnppApplication.getHNPPInstance().getHouseholdIdRepository();
            householdIdRepo.close(villageId,hhid);
            List<FamilyEventClient> familyEventClientList = new ArrayList<>();
            FamilyEventClient familyEventClient = HnppJsonFormUtils.processFamilyUpdateForm(Utils.context().allSharedPreferences(), jsonString);
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

        }
        return null;

    }


}
