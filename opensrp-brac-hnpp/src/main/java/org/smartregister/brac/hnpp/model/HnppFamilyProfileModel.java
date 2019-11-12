package org.smartregister.brac.hnpp.model;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.chw.core.model.CoreFamilyProfileModel;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.List;

public class HnppFamilyProfileModel extends CoreFamilyProfileModel {
    private String moduleId;
    private String houseHoldId;
    private String familyBaseEntityId;
    public HnppFamilyProfileModel(String familyName,String moduleId,String houseHoldId, String familyBaseEntityId) {
        super(familyName);
        this.moduleId = moduleId;
        this.houseHoldId = houseHoldId;
        this.familyBaseEntityId = familyBaseEntityId;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        HnppJsonFormUtils.updateFormWithMemberId(form,houseHoldId,familyBaseEntityId);
        HnppJsonFormUtils.updateFormWithModuleId(form,moduleId,familyBaseEntityId);
        HnppJsonFormUtils.updateFormWithSimPrintsEnable(form);

        return form;
    }

    @Override
    public FamilyEventClient processMemberRegistration(String jsonString, String familyBaseEntityId) {
        FamilyEventClient familyEventClient = processRegistration(jsonString, familyBaseEntityId);
        EventClientRepository eventClientRepository = FamilyLibrary.getInstance().context().getEventClientRepository();
        try{
            JSONObject familyJSON = eventClientRepository.getClientByBaseEntityId(familyBaseEntityId);
            String addessJson = familyJSON.getString("addresses");
            JSONArray jsonArray = new JSONArray(addessJson);
            List<Address> listAddress = new ArrayList<>();
            for(int i = 0; i <jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Address address = new Gson().fromJson(jsonObject.toString(), Address.class);
                listAddress.add(address);
            }
            Client familyClient = familyEventClient.getClient();

            familyClient.setAddresses(listAddress);
        }catch (Exception e){

        }

        return familyEventClient;
    }
    public FamilyEventClient processUpdateMemberRegistration(String jsonString, String familyBaseEntityId) {
        FamilyEventClient familyEventClient = HnppJsonFormUtils.processFamilyMemberForm(FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, familyBaseEntityId,Utils.metadata().familyMemberRegister.updateEventType);
        EventClientRepository eventClientRepository = FamilyLibrary.getInstance().context().getEventClientRepository();
        try{
            JSONObject familyJSON = eventClientRepository.getClientByBaseEntityId(familyBaseEntityId);
            String addessJson = familyJSON.getString("addresses");
            JSONArray jsonArray = new JSONArray(addessJson);
            List<Address> listAddress = new ArrayList<>();
            for(int i = 0; i <jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Address address = new Gson().fromJson(jsonObject.toString(), Address.class);
                listAddress.add(address);
            }
            Client familyClient = familyEventClient.getClient();

            familyClient.setAddresses(listAddress);
        }catch (Exception e){

        }
        if (familyEventClient == null) {
            return null;
        } else {
            this.updateWra(familyEventClient);
            return familyEventClient;
        }
    }
    private FamilyEventClient processRegistration(String jsonString, String familyBaseEntityId) {
        FamilyEventClient familyEventClient = HnppJsonFormUtils.processFamilyMemberForm(FamilyLibrary.getInstance().context().allSharedPreferences(), jsonString, familyBaseEntityId,Utils.metadata().familyMemberRegister.registerEventType);
        if (familyEventClient == null) {
            return null;
        } else {
            this.updateWra(familyEventClient);
            return familyEventClient;
        }
    }


}
