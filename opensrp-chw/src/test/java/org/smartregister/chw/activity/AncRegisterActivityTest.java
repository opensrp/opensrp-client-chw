package org.smartregister.chw.activity;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.fragment.AncRegisterFragment;

import static org.smartregister.chw.core.utils.CoreConstants.EventType.ANC_REGISTRATION;

public class AncRegisterActivityTest extends BaseActivityTest<AncRegisterActivity> {

    @Test
    public void getRegisterActivityReturnsCorrectActivity() {
        Assert.assertEquals(AncRegisterActivity.class, activity.getRegisterActivity(ANC_REGISTRATION));
    }

    @Test
    public void getRegisterFragmentReturnsCorrectFragment() {
        Assert.assertTrue(getActivity().getRegisterFragment() instanceof AncRegisterFragment);
    }

    @Override
    protected Class<AncRegisterActivity> getActivityClass() {
        return AncRegisterActivity.class;
    }
}
