package org.smartregister.brac.hnpp.activity;

import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.listener.HnppFamilyBottomNavListener;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.activity.CoreChildRegisterActivity;
import org.smartregister.brac.hnpp.fragment.HnppChildRegisterFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends CoreChildRegisterActivity {
    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new HnppChildRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, Utils.metadata().familyFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setName(getString(R.string.add_family));
        form.setSaveLabel(getString(R.string.save));
        form.setWizard(false);
        if(!HnppConstants.isReleaseBuild()){
            form.setActionBarBackground(R.color.test_app_color);

        }else{
            form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);

        }
        form.setNavigationBackground(org.smartregister.family.R.color.family_navigation);
        form.setHomeAsUpIndicator(org.smartregister.family.R.mipmap.ic_cross_white);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);
        bottomNavigationView.setOnNavigationItemSelectedListener(new HnppFamilyBottomNavListener(this, bottomNavigationView));
    }
}
