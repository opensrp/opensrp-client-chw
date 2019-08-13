package org.smartregister.chw.activity;

import android.content.Intent;

import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.utils.CoreConstants;

import org.smartregister.chw.BaseActivityTest;

public class FamilyProfileMenuActivityTest extends BaseActivityTest<CoreFamilyProfileMenuActivity> {
    @Override
    protected Class<CoreFamilyProfileMenuActivity> getActivityClass() {
        return CoreFamilyProfileMenuActivity.class;
    }

    @Override
    protected Intent getControllerIntent() {
        Intent intent = new Intent();
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, "12345");
        intent.putExtra(CoreFamilyProfileMenuActivity.MENU, CoreConstants.MenuType.ChangeHead);
        return intent;
    }
}
