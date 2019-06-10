package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.interactor.AncHomeVisitInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

public class AncHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, String memberBaseEntityID) {
        Intent intent = new Intent(activity, AncHomeVisitActivity.class);
        intent.putExtra("BASE_ENTITY_ID", memberBaseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(BASE_ENTITY_ID, this, new AncHomeVisitInteractor());
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
