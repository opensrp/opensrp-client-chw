package org.smartregister.chw.activity;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getOutOfAreaChildForm;
import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static org.smartregister.chw.util.Constants.BASE_ENTITY_ID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.OutOfAreaFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtilsFlv;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class OutOfAreaChildUpdateFormActivity extends OutOfAreaChildActivity {

    public CommonPersonObject commonPersonObject;

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        OutOfAreaChildUpdateFormActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
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
            JSONObject form = JsonFormUtilsFlv.getAutoPopulatedJsonEditFormString(getOutOfAreaChildForm(), this, getFamilyRegistrationDetails(Objects.requireNonNull(getIntent().getStringExtra(BASE_ENTITY_ID)).toLowerCase()), Constants.EncounterType.OUT_OF_AREA_CHILD_REGISTRATION);
            try {
                assert form != null;
                JsonFormUtilsFlv.startFormActivity(this, form, getResources().getString(R.string.out_of_area_form));
            } catch (Exception e) {
                Timber.e(e);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OutOfAreaFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // code
    }

    private CommonPersonObjectClient getFamilyRegistrationDetails(String entityId) {
        final CommonPersonObject personObject = getCommonRepository("ec_out_of_area_child")
                .findByBaseEntityId(entityId);
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(personObject.getCaseId(),
                personObject.getDetails(), "");
        commonPersonObjectClient.setColumnmaps(personObject.getColumnmaps());
        commonPersonObjectClient.setDetails(personObject.getDetails());
        return commonPersonObjectClient;
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                presenter().saveOutOfAreaForm(jsonString, true);
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            Utils.launchAndClearOldInstanceOfActivity(this, OutOfAreaChildActivity.class);
        }
    }

    @Override
    public void openFamilyListView() {
        // Do nothing
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        // Do nothing
    }
}