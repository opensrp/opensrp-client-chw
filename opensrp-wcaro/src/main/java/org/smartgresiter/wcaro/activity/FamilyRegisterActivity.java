package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.Fragment;

import org.json.JSONObject;
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.fragment.FamilyRegisterFragment;
import org.smartgresiter.wcaro.listener.WCAROBottomNavigationListener;
import org.smartgresiter.wcaro.model.FamilyRegisterModel;
import org.smartgresiter.wcaro.presenter.FamilyRegisterPresenter;
import org.smartgresiter.wcaro.util.NavigationHelper;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.listener.FamilyBottomNavigationListener;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends BaseFamilyRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new FamilyRegisterPresenter(this, new FamilyRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void registerBottomNavigation() {
//        super.registerBottomNavigation();

        if (!BuildConfig.SCAN_QR_CODE) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_library);

            bottomNavigationView.inflateMenu(org.smartregister.family.R.menu.bottom_nav_family_menu);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            WCAROBottomNavigationListener familyBottomNavigationListener = new WCAROBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationHelper.getInstance(this, null, null);
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, Utils.metadata().nativeFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }



}
