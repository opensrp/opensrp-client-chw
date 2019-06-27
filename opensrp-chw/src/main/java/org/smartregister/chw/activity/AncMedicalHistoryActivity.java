package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.activity.BaseAncMedicalHistoryActivity;
import org.smartregister.chw.anc.domain.MemberObject;

import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT;

public class AncMedicalHistoryActivity extends BaseAncMedicalHistoryActivity {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, AncMedicalHistoryActivity.class);
        intent.putExtra(MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }
}
