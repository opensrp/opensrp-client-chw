package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreAllClientsRegisterActivity;
import org.smartregister.chw.core.presenter.CoreAllClientsRegisterPresenter;
import org.smartregister.chw.fragment.AllClientsRegisterFragment;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.util.FormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class AllClientsRegisterActivity extends CoreAllClientsRegisterActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FormUtils formUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChwApplication.getInstance().notifyAppContextChange();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AllClientsRegisterFragment();
    }

    @Override
    public void startRegistration() {
        startFormActivity(getFormUtils().getFormJson(Constants.ALL_CLIENT_REGISTRATION_FORM));
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, BaseOpdFormActivity.class);
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonObject.toString());

        Form form = new Form();
        form.setName(getString(R.string.add_client));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setPreviousLabel(getResources().getString(R.string.back));
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
        bottomNavigationView.getMenu().findItem(R.id.action_register).setTitle(R.string.add_client).setIcon(R.drawable.ic_action_add);
    }

    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(
            @NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return new CoreAllClientsRegisterPresenter(view, model);
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_scan_qr:
                startQrCodeScanner();
                return true;
            case R.id.action_family:
                switchToBaseFragment();
                break;
            case R.id.action_register:
                startRegistration();
                break;
            default:
                return true;
        }
        return true;
    }

    public FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(this);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }
}
