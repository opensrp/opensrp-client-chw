package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getOutOfAreaDeathForm;
import static org.smartregister.chw.core.utils.FormUtils.getFormUtils;
import static org.smartregister.chw.util.Constants.Postfixes.OUT_OF_AREA_DEATH;
import static org.smartregister.chw.util.CrvsConstants.UNIQUE_ID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.fragment.OutOfAreaDeathFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtilsFlv;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class OutOfAreaDeathUpdateActivity extends OutOfAreaDeathActivity {

    public CommonPersonObject commonPersonObject;

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        OutOfAreaDeathUpdateActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        startAncDangerSignsOutcomeForm();
    }

    public void startAncDangerSignsOutcomeForm() {
        try {
            JSONObject formJsonObject = getFormUtils().getFormJson(getOutOfAreaDeathForm());
            String openSRPId = AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId();
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put(UNIQUE_ID, openSRPId.replace("-", "") + OUT_OF_AREA_DEATH);
            CoreJsonFormUtils.populateJsonForm(formJsonObject, valueMap);
            JsonFormUtilsFlv.startFormActivity(OutOfAreaDeathUpdateActivity.this, formJsonObject, getResources().getString(R.string.out_of_area_form));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OutOfAreaDeathFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // code
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                assert jsonString != null;
                JSONObject form = new JSONObject(jsonString);

                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EncounterType.OUT_OF_AREA_DEATH_REGISTRATION)
                ) {
                    presenter().saveOutOfAreaDeathForm(jsonString, false);
                }

            } catch (Exception e) {
                Timber.e(e);
            }

        } else {
            Utils.launchAndClearOldInstanceOfActivity(this, OutOfAreaDeathActivity.class);
        }
    }
}