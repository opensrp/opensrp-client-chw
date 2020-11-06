package org.smartregister.chw.activity;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;

public class ChildProfileActivityFlvTest {

    private ChildProfileActivity.Flavor childProfileActivityFlv;
    private CommonPersonObjectClient commonPersonObjectClient;
    HashMap<String, String> columnMaps = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childProfileActivityFlv = Mockito.mock(DefaultChildProfileActivityFlv.class, Mockito.CALLS_REAL_METHODS);
        HashMap<String, String> clientDetails = new HashMap<>();
        commonPersonObjectClient = new CommonPersonObjectClient(
                "test-case-id",
                clientDetails,
                "tester");
    }

    @Test
    public void getToolBarTitleNameReturnsFirstName() {
        MemberObject memberObject = new MemberObject();
        memberObject.setFirstName("Obafemi");
        Assert.assertEquals("Obafemi", childProfileActivityFlv.getToolbarTitleName(memberObject));
    }

    @Test
    public void isChildOverTwoMonthsReturnsTrueWhenChildIsOver() {
        LocalDate localDate = LocalDate.now().minusMonths(3);
        String dateString = localDate.toString("yyyy-MM-dd");
        columnMaps.put(DBConstants.KEY.DOB, dateString);
        commonPersonObjectClient.setColumnmaps(columnMaps);
        Assert.assertTrue(childProfileActivityFlv.isChildOverTwoMonths(commonPersonObjectClient));
    }


    @Test
    public void isChildOverTwoMonthsReturnsTWhenChildIsUnder() {
        LocalDate localDate = LocalDate.now();
        String dateString = localDate.toString("yyyy-MM-dd");
        columnMaps.put(DBConstants.KEY.DOB, dateString);
        commonPersonObjectClient.setColumnmaps(columnMaps);
        Assert.assertFalse(childProfileActivityFlv.isChildOverTwoMonths(commonPersonObjectClient));
    }
}
