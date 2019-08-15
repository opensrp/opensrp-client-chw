package org.smartregister.chw.core.model;

import org.json.JSONObject;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class CoreChildProfileModel implements CoreChildProfileContract.Model {

    private FormUtils formUtils;
    private String familyName;

    public CoreChildProfileModel(String familyName) {
        this.familyName = familyName;
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        form = CoreJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
        if (formName.equals(CoreConstants.JSON_FORM.getChildRegister())) {
            CoreJsonFormUtils.updateJsonForm(form, familyName);
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
