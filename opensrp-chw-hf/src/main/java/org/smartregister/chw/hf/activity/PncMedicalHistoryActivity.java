package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CorePncMedicalHistoryActivity;
import org.smartregister.chw.hf.interactor.PncMedicalHistoryActivityInteractor;

public class PncMedicalHistoryActivity extends CorePncMedicalHistoryActivity {
    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, PncMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected BaseAncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor() {
        return new PncMedicalHistoryActivityInteractor();
    }
}
