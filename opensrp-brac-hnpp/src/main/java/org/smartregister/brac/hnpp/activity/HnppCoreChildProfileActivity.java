package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.opensrp.api.constants.Gender;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;

public class HnppCoreChildProfileActivity extends CoreChildProfileActivity {

    protected String houseHoldId = "";

    public static void startMe(Activity activity, String houseHoldId, boolean isComesFromFamily, MemberObject memberObject, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(HnppConstants.KEY.HOUSE_HOLD_ID,houseHoldId);
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, isComesFromFamily);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        houseHoldId = getIntent().getStringExtra(HnppConstants.KEY.HOUSE_HOLD_ID);
    }

    @Override
    protected void updateTopBar() {
        if (gender.equalsIgnoreCase(getString(R.string.male))) {
            imageViewProfile.setBorderColor(getResources().getColor(org.smartregister.chw.core.R.color.light_blue));
        } else if (gender.equalsIgnoreCase(getString(R.string.female))) {
            imageViewProfile.setBorderColor(getResources().getColor(org.smartregister.chw.core.R.color.light_pink));
        }
    }
}
