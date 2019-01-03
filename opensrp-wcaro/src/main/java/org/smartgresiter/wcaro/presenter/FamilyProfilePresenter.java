package org.smartgresiter.wcaro.presenter;

import android.util.Log;

import org.json.JSONObject;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.presenter.BaseFamilyProfilePresenter;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

public class FamilyProfilePresenter extends BaseFamilyProfilePresenter {

    FormUtils formUtils = null;


    public FamilyProfilePresenter(FamilyProfileContract.View loginView, FamilyProfileContract.Model model, String familyBaseEntityId) {
        super(loginView, model, familyBaseEntityId);
    }

    public void startForm(String formName, String entityId, String metadata, String currentLocationId, String familyid) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);

        form = JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyid);
        getView().startFormActivity(form);

    }

    private FormUtils getFormUtils() {

        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

}
