package org.smartregister.chw.activity;

import android.content.Intent;

import org.mockito.Mockito;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;

public class ChildProfileActivityTest extends BaseActivityTest<ChildProfileActivity> {
    @Override
    protected Class<ChildProfileActivity> getActivityClass() {
        return ChildProfileActivity.class;
    }

    @Override
    protected Intent getControllerIntent() {
        Intent activityIntent = new Intent();
        MemberObject memberObject = Mockito.mock(MemberObject.class);
        activityIntent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        return activityIntent;
    }
}
