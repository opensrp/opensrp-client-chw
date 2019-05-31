package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.MemberObject;

public class AncMemberProfileActivity extends BaseAncMemberProfileActivity {
    public static void startMe(Activity activity, MemberObject memberObject) {

        Intent intent = new Intent(activity, AncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }
}
