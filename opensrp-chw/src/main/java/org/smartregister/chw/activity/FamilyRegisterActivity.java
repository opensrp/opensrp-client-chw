package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.FamilyRegisterFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.referral.ReferralLibrary;
import org.smartregister.chw.util.Constants;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity {

    public static void startFamilyRegisterForm(Activity activity) {
        Intent intent = new Intent(activity, FamilyRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        activity.startActivity(intent);
    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper,
            BottomNavigationView bottomNavigationView,
            Activity activity
    ) {
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper,
            BottomNavigationView bottomNavigationView,
            BottomNavigationView.OnNavigationItemSelectedListener listener
    ) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        }

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasQR())
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasJobAids())
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasReports())
            bottomNavigationView.getMenu().removeItem(R.id.action_report);
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
        if (ChwApplication.getApplicationFlavor().hasReferrals()) {
            try {
                ReferralLibrary.getInstance().loadReferralServiceIndicators();
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }
}