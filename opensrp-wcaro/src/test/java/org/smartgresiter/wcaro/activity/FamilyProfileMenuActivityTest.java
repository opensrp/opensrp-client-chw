package org.smartgresiter.wcaro.activity;

import android.content.Intent;

import org.smartgresiter.wcaro.BaseActivityTest;
import org.smartgresiter.wcaro.util.Constants;

public class FamilyProfileMenuActivityTest extends BaseActivityTest<FamilyProfileMenuActivity> {
    @Override
    protected Class<FamilyProfileMenuActivity> getActivityClass() {
        return FamilyProfileMenuActivity.class;
    }

    @Override
    protected Intent getControllerIntent() {
        Intent intent = new Intent();
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, "12345");
        intent.putExtra(FamilyProfileMenuActivity.MENU, Constants.MenuType.ChangeHead);
        return intent;
    }
}
