package org.smartregister.chw.core.model;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.family.model.BaseFamilyProfileModel;

public abstract class CoreFamilyProfileModel extends BaseFamilyProfileModel {

    public CoreFamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = super.getFormAsJson(formName, entityId, currentLocationId);
        CoreJsonFormUtils.populateLocationsTree(form, JsonFormConstants.STEP1, CoreChwApplication.getInstance().getAllowedLocationLevels(), "nearest_facility");
        return form;
    }
}