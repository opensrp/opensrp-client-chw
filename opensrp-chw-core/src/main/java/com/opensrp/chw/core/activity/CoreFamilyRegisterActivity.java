package com.opensrp.chw.core.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.opensrp.chw.core.custom_views.NavigationMenu;
import com.opensrp.chw.core.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class CoreFamilyRegisterActivity extends BaseFamilyRegisterActivity {
    private String action = null;
    protected NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);

    @Override
    protected void initializePresenter() {
        presenter = new BaseFamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return null;
    }


    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        navigationMenu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.ALL_FAMILIES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode != RESULT_OK && StringUtils.isNotBlank(action)) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.RQ_CODE.STORAGE_PERMISIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (navigationMenu != null) {
                navigationMenu.startP2PActivity(this);
            }
        }
    }
}
