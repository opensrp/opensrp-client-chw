package org.smartregister.chw.model;

import com.opensrp.chw.core.utils.Constants;

import org.json.JSONObject;
import org.smartregister.chw.contract.ChildProfileContract;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class ChildProfileModel implements ChildProfileContract.Model {

    private FormUtils formUtils;
    private String familyName;

    public ChildProfileModel(String familyName) {
        this.familyName = familyName;
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        form = JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
        if (formName.equals(Constants.JSON_FORM.getChildRegister())) {
            JsonFormUtils.updateJsonForm(form, familyName);
        }

        return form;
    }


    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }
}
