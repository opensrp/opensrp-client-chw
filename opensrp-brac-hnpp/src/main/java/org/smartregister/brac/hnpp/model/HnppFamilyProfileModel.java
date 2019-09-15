package org.smartregister.brac.hnpp.model;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.utils.JsonFormUtils;
import org.smartregister.chw.core.model.CoreFamilyProfileModel;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.domain.FamilyEventClient;

public class HnppFamilyProfileModel extends CoreFamilyProfileModel {
    private String moduleId;
    private String houseHoldId;
    public HnppFamilyProfileModel(String familyName,String moduleId,String houseHoldId) {
        super(familyName);
        this.moduleId = moduleId;
        this.houseHoldId = houseHoldId;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        JsonFormUtils.updateFormWithMemberId(form,houseHoldId);
        JsonFormUtils.updateFormWithModuleId(form,moduleId);

        return form;
    }

}
