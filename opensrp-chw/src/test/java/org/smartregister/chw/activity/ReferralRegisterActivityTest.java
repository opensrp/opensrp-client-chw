package org.smartregister.chw.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.smartregister.chw.BaseUnitTest;

public class ReferralRegisterActivityTest extends BaseUnitTest {

    private ReferralRegisterActivity referralRegisterActivity;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        referralRegisterActivity = Robolectric.buildActivity(ReferralRegisterActivity.class).get();
        referralRegisterActivity.registerBottomNavigation();
    }

    @Test
    public void testGetRegisterFragment(){
        Assert.assertNotNull(referralRegisterActivity.getRegisterFragment());
    }

    @Test
    public void testGetOtherFragments(){
        Assert.assertNotNull(referralRegisterActivity.getOtherFragments());
        Assert.assertEquals(0, referralRegisterActivity.getOtherFragments().length);
    }

    @Test
    public void testGetViewIdentifiers(){
        Assert.assertEquals(1, referralRegisterActivity.getViewIdentifiers().size());
    }
}
