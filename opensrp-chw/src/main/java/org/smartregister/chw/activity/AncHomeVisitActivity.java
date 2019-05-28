package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.Constants.JSON_FORM.ANC_HOME_VISIT;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

public class AncHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, String memberBaseEntityID) {
        Intent intent = new Intent(activity, AncHomeVisitActivity.class);
        intent.putExtra("BASE_ENTITY_ID", memberBaseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected void initializeActions() throws BaseAncHomeVisitAction.ValidationException {
        actionList.put("Danger Signs", new BaseAncHomeVisitAction("Danger Signs", "", false, null, ANC_HOME_VISIT.DANGER_SIGNS));
        actionList.put("ANC Counseling", new BaseAncHomeVisitAction("ANC Counseling", "", false, null, ANC_HOME_VISIT.ANC_COUNSELING));

        BaseAncHomeVisitFragment fragmentLLTIN = BaseAncHomeVisitFragment.getInstance(this, "Sleeping under a LLITN",
                "Is the woman sleeping under a Long Lasting Insecticide-Treated Net (LLITN)?",
                R.drawable.form_llitn,
                BaseAncHomeVisitFragment.QuestionType.BOOLEAN
        );
        actionList.put("Sleeping under a LLITN", new BaseAncHomeVisitAction("Sleeping under a LLITN", "", false, fragmentLLTIN, null));

        BaseAncHomeVisitFragment ancCardFragment = BaseAncHomeVisitFragment.getInstance(this, "ANC Card Received",
                "ANC card received?",
                org.smartregister.chw.opensrp_chw_anc.R.drawable.avatar_woman,
                BaseAncHomeVisitFragment.QuestionType.BOOLEAN
        );
        actionList.put("ANC Card Received", new BaseAncHomeVisitAction("ANC Card Received", "", false, ancCardFragment, null));

        actionList.put("ANC Health Facility Visit 1", new BaseAncHomeVisitAction("ANC Health Facility Visit 1", "", false, null, "anc"));
        actionList.put("TT Immunization 1", new BaseAncHomeVisitAction("TT Immunization 1", "", false, null, "anc"));
        actionList.put("IPTp-SP dose 1", new BaseAncHomeVisitAction("IPTp-SP dose 1", "", false, null, "anc"));
        actionList.put("Observation & Illness", new BaseAncHomeVisitAction("Observation & Illness", "", true, null, "anc"));
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
