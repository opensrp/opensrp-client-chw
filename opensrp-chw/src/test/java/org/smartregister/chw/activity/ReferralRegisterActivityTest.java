package org.smartregister.chw.activity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.chw.BaseUnitTest;

public class ReferralRegisterActivityTest extends BaseUnitTest {

    private ReferralRegisterActivity referralRegisterActivity;
    private ActivityController<ReferralRegisterActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(ReferralRegisterActivity.class);
        referralRegisterActivity = controller.get();
        referralRegisterActivity.registerBottomNavigation();
    }

    @Test
    public void testGetRegisterFragment() {
        Assert.assertNotNull(referralRegisterActivity.getRegisterFragment());
    }

    @Test
    public void testGetOtherFragments() {
        Assert.assertNotNull(referralRegisterActivity.getOtherFragments());
        Assert.assertEquals(0, referralRegisterActivity.getOtherFragments().length);
    }

    @Test
    public void testGetViewIdentifiers() {
        Assert.assertEquals(1, referralRegisterActivity.getViewIdentifiers().size());
    }

    @After
    public void tearDown() {
        try {
            referralRegisterActivity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
