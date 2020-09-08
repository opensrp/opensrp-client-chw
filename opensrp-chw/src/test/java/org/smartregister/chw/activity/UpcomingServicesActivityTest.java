package org.smartregister.chw.activity;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.application.ChwApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class UpcomingServicesActivityTest {

    @Test
    public void filterDueTodayServiceListShouldReturnDueToday() {
        UpcomingServicesActivity upcomingServicesActivity = Mockito.spy(UpcomingServicesActivity.class);

        List<BaseUpcomingService> todayServiceList = upcomingServicesActivity.filterDueTodayServiceList(getDummyServiceList());
        Assert.assertEquals(1, todayServiceList.size());
    }

    private List<BaseUpcomingService> getDummyServiceList() {
        List<BaseUpcomingService> serviceList = new ArrayList<>();
        BaseUpcomingService baseUpcomingService = new BaseUpcomingService();
        baseUpcomingService.setServiceDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        serviceList.add(baseUpcomingService);
        return serviceList;
    }
}
