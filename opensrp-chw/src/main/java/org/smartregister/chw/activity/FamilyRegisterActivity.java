package org.smartregister.chw.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity implements SyncStatusBroadcastReceiver.SyncStatusListener {
    private ProgressDialog progressDialog;

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
        try {
            SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
        }
        catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(getString(R.string.syncing_records));
    }

    private void showProgressDialog() {
        try {
            if (progressDialog == null) {
                initializeProgressDialog();
            }

            progressDialog.show();
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void onSyncStart() {
        showProgressDialog();
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        showProgressDialog();
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
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
            return currentConfig;
        }
}