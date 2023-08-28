package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.sbc.SbcLibrary;
import org.smartregister.chw.sbc.activity.BaseSbcProfileActivity;
import org.smartregister.chw.sbc.domain.MemberObject;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc.util.VisitUtils;

import timber.log.Timber;

public class SbcMemberProfileActivity extends BaseSbcProfileActivity {
    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, SbcMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void recordSbc(MemberObject memberObject) {
        SbcVisitActivity.startMe(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        try {
            VisitUtils.processVisits(SbcLibrary.getInstance().visitRepository(), SbcLibrary.getInstance().visitDetailsRepository(), SbcMemberProfileActivity.this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void openMedicalHistory() {
        SbcMedicalHistoryActivity.startMe(this, memberObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
    }
}
