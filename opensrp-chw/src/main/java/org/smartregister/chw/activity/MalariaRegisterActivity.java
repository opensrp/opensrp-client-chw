package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.MalariaRegisterFragment;
import org.smartregister.chw.malaria.activity.BaseMalariaRegisterActivity;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getMalariaConfirmation;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.isMultiPartForm;

public class MalariaRegisterActivity extends BaseMalariaRegisterActivity {

    public static void startMalariaRegistrationActivity(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, MalariaRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.malaria.util.Constants.ACTIVITY_PAYLOAD.MALARIA_FORM_NAME, getMalariaConfirmation());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD.ACTION, org.smartregister.chw.anc.util.Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        if (isMultiPartForm(jsonForm)) {
            form.setWizard(true);
            form.setNavigationBackground(R.color.family_navigation);
            form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
            form.setName(this.getString(R.string.malaria_registration));
            form.setNextLabel(this.getResources().getString(R.string.next));
            form.setPreviousLabel(this.getResources().getString(R.string.back));
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.MALARIA_REGISTER);
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MalariaRegisterFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter()
                    .setSelectedView(Constants.DrawerMenu.MALARIA);
        }
    }
}