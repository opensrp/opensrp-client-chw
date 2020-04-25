package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.dataloader.FPDataLoader;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp_pathfinder.activity.BaseFpRegisterActivity;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;
import org.smartregister.chw.fragment.PathfinderFamilyPlanningRegisterFragment;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.isMultiPartForm;

public class PathfinderFamilyPlanningRegisterActivity extends BaseFpRegisterActivity {

    private static String baseEntityId;

    public static void startFpRegistrationActivity(Activity activity, String baseEntityID, String dob, String formName, String payloadType) {
        Timber.e("coze starting family planning activity");
        Intent intent = new Intent(activity, PathfinderFamilyPlanningRegisterActivity.class);
        intent.putExtra(FamilyPlanningConstants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(FamilyPlanningConstants.ActivityPayload.DOB, dob);
        intent.putExtra(FamilyPlanningConstants.ActivityPayload.FP_FORM_NAME, formName);
        intent.putExtra(FamilyPlanningConstants.ActivityPayload.ACTION, payloadType);
        baseEntityId = baseEntityID;
        activity.startActivity(intent);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    public void onFormSaved() {
        startActivity(new Intent(this, PathfinderFamilyPlanningRegisterActivity.class));
        super.onFormSaved();
        this.finish();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PathfinderFamilyPlanningRegisterFragment();
    }

    @Override
    protected Activity getFpRegisterActivity() {
        return this;
    }

    @Override
    public JSONObject getFpFormForEdit() {

        NativeFormsDataBinder binder = new NativeFormsDataBinder(this, baseEntityId);
        binder.setDataLoader(new FPDataLoader(getString(R.string.fp_update_family_planning)));

        JSONObject form = binder.getPrePopulatedForm(FamilyPlanningConstants.Forms.FAMILY_PLANNING_REGISTRATION_FORM);
        try {
            form.put(JsonFormUtils.ENCOUNTER_TYPE, FamilyPlanningConstants.EventType.UPDATE_FAMILY_PLANNING_REGISTRATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return form;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Timber.e("coze loading the form");
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setWizard(false);
        form.setHomeAsUpIndicator(org.smartregister.chw.core.R.mipmap.ic_cross_white);
        form.setSaveLabel(getString(org.smartregister.chw.core.R.string.submit));

        if (isMultiPartForm(jsonForm)) {
            form.setWizard(true);
            form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
            form.setName(this.getString(org.smartregister.chw.core.R.string.fp_registration));
            form.setNextLabel(this.getResources().getString(org.smartregister.chw.core.R.string.next));
            form.setPreviousLabel(this.getResources().getString(org.smartregister.chw.core.R.string.back));
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        Timber.e("coze the form: " + form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.FAMILY_PLANNING);
        }
    }
}