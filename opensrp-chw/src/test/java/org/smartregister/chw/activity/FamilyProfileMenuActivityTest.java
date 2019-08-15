package org.smartregister.chw.activity;

import android.content.Intent;

import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.utils.CoreConstants;

public class FamilyProfileMenuActivityTest extends BaseActivityTest<CoreFamilyProfileMenuActivity> {
    @Override
    protected Intent getControllerIntent() {
        Intent intent = new Intent();
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, "12345");
        intent.putExtra(CoreFamilyProfileMenuActivity.MENU, CoreConstants.MenuType.ChangeHead);
        return intent;
    }

    @Override
    protected Class<CoreFamilyProfileMenuActivity> getActivityClass() {
        return CoreFamilyProfileMenuActivity.class;
    }
}
