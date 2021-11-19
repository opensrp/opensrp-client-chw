package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vijay.jsonwizard.domain.Form;

import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.FamilyRegisterFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.presenter.FamilyRegisterPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity {

    public static void startFamilyRegisterForm(Activity activity) {
        Intent intent = new Intent(activity, FamilyRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        activity.startActivity(intent);
    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
        if (!ChwApplication.getApplicationFlavor().showBottomNavigation()
                && bottomNavigationView != null){
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new FamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }


    @Override
    public Form getFormConfig() {
        Form currentConfig =  super.getFormConfig();
        if (ChwApplication.getApplicationFlavor().hideFamilyRegisterPreviousNextIcons()){
            currentConfig.setHidePreviousIcon(true);
            currentConfig.setHideNextIcon(true);
        }
        if (ChwApplication.getApplicationFlavor().showFamilyRegisterNextInToolbar()){
            currentConfig.setHideNextButton(true);
            currentConfig.setNextLabel(getString(R.string.next));
            currentConfig.setShowNextInToolbarWhenWizard(true);
        }
        currentConfig.setGreyOutSaveWhenFormInvalid(ChwApplication.getApplicationFlavor().greyOutFormActionsIfInvalid());
        return currentConfig;
    }
}