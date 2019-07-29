package com.opensrp.chw.core.activity;

import android.support.v4.view.ViewPager;

import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;

import org.smartregister.family.activity.BaseFamilyProfileActivity;

public class CoreFamilyProfileActivity extends BaseFamilyProfileActivity implements FamilyProfileExtendedContract.View {
    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

    }

    @Override
    public void updateHasPhone(boolean hasPhone) {

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

}
