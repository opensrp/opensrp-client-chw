package org.smartregister.chw.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ChildProfileActivityFlvTest {

    private ChildProfileActivity.Flavor childProfileActivityFlv;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childProfileActivityFlv = Mockito.mock(ChildProfileActivityFlv.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getToolBarTitleNameReturnsFamilyName() {
        MemberObject memberObject = new MemberObject();
        memberObject.setFamilyName("Obafemi");
        Assert.assertEquals("Obafemi", childProfileActivityFlv.getToolbarTitleName(memberObject));
    }

    @Test
    public void isChildOverTwoMonthsReturnsTrue() {
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(
                "test-case-id",
                null,
                "tester");
        Assert.assertTrue(childProfileActivityFlv.isChildOverTwoMonths(commonPersonObjectClient));
    }
}
