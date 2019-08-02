package com.opensrp.chw.hf.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.opensrp.chw.core.activity.CoreChildProfileActivity;
import com.opensrp.chw.core.model.CoreChildProfileModel;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.presenter.HfChildProfilePresenter;

import org.smartregister.family.util.Constants;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                fetchProfileData();

            }
        }
    };

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        fetchProfileData();
    }

    @Override
    protected void initializePresenter() {
        childBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        isComesFromFamily = getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new HfChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDateTimeChangedReceiver);
        handler.removeCallbacksAndMessages(null);
    }
}
