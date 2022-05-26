package org.smartregister.chw.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.application.ChwApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UpcomingServicesUtilTest extends BaseUnitTest {

    @Mock
    private MemberObject memberObject;

    @Mock
    private Context androidContext;

    private final Random random = new Random();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAgeInMonths(){
        Mockito.when(memberObject.getDob()).thenReturn("2021-07-28");
        Assert.assertNotNull(UpcomingServicesUtil.getAgeInMonths(memberObject));
    }

    @Test
    public void testDeepCopyNull(){
        Assert.assertNull(UpcomingServicesUtil.deepCopy(null));
    }

    @Test
    public void testDeepCopyReturnsCopy(){
        BaseUpcomingService upcomingService = Mockito.mock(BaseUpcomingService.class);
        List<BaseUpcomingService> baseUpcomingServiceList = new ArrayList<>();
        baseUpcomingServiceList.add(upcomingService);

        List<BaseUpcomingService> copy = UpcomingServicesUtil.deepCopy(baseUpcomingServiceList);
        BaseUpcomingService baseUpcomingService2 = copy.get(0);

        Assert.assertEquals(upcomingService.getServiceName(), baseUpcomingService2.getServiceName());
        Assert.assertEquals(upcomingService.getOverDueDate(), baseUpcomingService2.getOverDueDate());
        Assert.assertEquals(upcomingService.getExpiryDate(), baseUpcomingService2.getExpiryDate());
        Assert.assertEquals(upcomingService.getOverDueDate(), baseUpcomingService2.getServiceDate());
    }


    @Test
    public void testShowStatusForChild(){
        Mockito.when(memberObject.getAge()).thenReturn(random.nextInt(20));
        int age = memberObject.getAge();
        String gender = new String[]{"female", "male"}[random.nextInt(2)];
        boolean expected = (ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus()
                && (age < 2 || (gender.equalsIgnoreCase("Female") && age >= 9 && age <= 11))
                || !ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus());

        Assert.assertEquals(expected, UpcomingServicesUtil.showStatusForChild(memberObject, gender));
    }

    @Test
    public void testHasUpcomingServicesForAgeNull(){
        Mockito.when(memberObject.getDob()).thenReturn("");
        Assert.assertNull(UpcomingServicesUtil.getAgeInMonths(memberObject));
        Assert.assertFalse(UpcomingServicesUtil.hasUpcomingDueServices(memberObject, androidContext));
    }
}