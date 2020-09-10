package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class UpcomingServicesActivityTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartMe() {
        Activity activity = Mockito.mock(Activity.class);
        UpcomingServicesActivity.startMe(activity, getMemberObject());
        Mockito.verify(activity).startActivity(Mockito.any(Intent.class));
    }

    @Test
    public void updateUiShouldUpdateComponentsVisibility() {
        UpcomingServicesActivity upcomingServicesActivity = Mockito.spy(UpcomingServicesActivity.class);

        CustomFontTextView textView = Mockito.spy(new CustomFontTextView(RuntimeEnvironment.application));
        RecyclerView recyclerView = Mockito.spy(new RecyclerView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(upcomingServicesActivity, "todayServicesTV", textView);
        ReflectionHelpers.setField(upcomingServicesActivity, "dueTodayRV", recyclerView);

        upcomingServicesActivity.filterAndPopulateDueTodayServices(getDummyServiceList());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals(View.VISIBLE, recyclerView.getVisibility());
    }

    @Test
    public void filterDueTodayServiceListShouldReturnDueToday() {
        UpcomingServicesActivity upcomingServicesActivity = Mockito.spy(UpcomingServicesActivity.class);

        List<BaseUpcomingService> todayServiceList = upcomingServicesActivity.filterDueTodayServices(getDummyServiceList());
        Assert.assertEquals(1, todayServiceList.size());
    }

    private List<BaseUpcomingService> getDummyServiceList() {
        List<BaseUpcomingService> serviceList = new ArrayList<>();
        BaseUpcomingService baseUpcomingService = new BaseUpcomingService();
        baseUpcomingService.setServiceDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        serviceList.add(baseUpcomingService);
        return serviceList;
    }

    private MemberObject getMemberObject() {
        MemberObject memberObject = new MemberObject();
        memberObject.setFirstName("");
        memberObject.setMiddleName("");
        memberObject.setLastName("");
        return memberObject;
    }

}
