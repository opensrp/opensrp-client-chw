package org.smartregister.brac.hnpp.model;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.utils.HnppJsonFormUtils;
import org.smartregister.chw.core.model.CoreFamilyProfileModel;

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
        HnppJsonFormUtils.updateFormWithModuleId(form,moduleId);

        return form;
    }

}
