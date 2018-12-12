package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONObject;
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.fragment.ChildRegisterFragment;
import org.smartgresiter.wcaro.listener.ChildBottomNavigationListener;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.presenter.ChildRegisterPresenter;
import org.smartregister.AllConstants;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;

public class ChildRegisterActivity extends BaseRegisterActivity implements ChildRegisterContract.View {
    @Override
    protected void initializePresenter() {
        presenter = new ChildRegisterPresenter(this, new ChildRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startRegistration() {
        //TODO need to change the hard code
        startFormActivity("child_enrollment", null, null);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof ChildRegisterFragment) {
                String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(formName, entityId, metaData, locationId,"");
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, Utils.metadata().nativeFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
//        startRegistration();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.registerEventType)
                        || form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals("Child Registration")
                        ) {
                    presenter().saveForm(jsonString, false);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        }
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_search);
            bottomNavigationView.getMenu().removeItem(R.id.action_library);

            bottomNavigationView.inflateMenu(R.menu.bottom_nav_family_menu);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            ChildBottomNavigationListener childBottomNavigationListener = new ChildBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(childBottomNavigationListener);

        }
        if (!BuildConfig.SCAN_QR_CODE) {
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Utils.metadata().familyRegister.config);
    }

    @Override
    public ChildRegisterContract.Presenter presenter() {
        return (ChildRegisterContract.Presenter) presenter;
    }
}
