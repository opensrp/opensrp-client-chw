package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.brac.hnpp.utils.HnppMemberObject;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;

public class HnppCoreChildProfileActivity extends CoreChildProfileActivity {
    protected HnppMemberObject memberObject;


    public static void startMe(Activity activity, boolean isComesFromFamily, HnppMemberObject memberObject, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, isComesFromFamily);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            memberObject = (HnppMemberObject) getIntent().getSerializableExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT);
            childBaseEntityId = memberObject.getBaseEntityId();
            isComesFromFamily = getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        }
    }
}
