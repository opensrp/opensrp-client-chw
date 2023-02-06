package org.smartregister.chw.activity;

import android.content.Intent;

import org.mockito.Mockito;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Map;

public class AllClientsMemberProfileActivityTest  extends BaseActivityTest<AllClientsMemberProfileActivity> {

    @Override
    protected Class<AllClientsMemberProfileActivity> getActivityClass() {
        return AllClientsMemberProfileActivity.class;
    }

    @Override
    protected Intent getControllerIntent() {
        Intent activityIntent = new Intent();
        CommonPersonObjectClient commonPersonObjectClient = Mockito.mock(CommonPersonObjectClient.class);
        Map<String, String> columnMaps = new HashMap<>();
        columnMaps.put(CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER, "123344");
        Mockito.when(commonPersonObjectClient.getColumnmaps()).thenReturn(columnMaps);
        activityIntent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, commonPersonObjectClient);
        return activityIntent;
    }
}