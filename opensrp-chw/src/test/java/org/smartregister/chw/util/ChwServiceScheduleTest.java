package org.smartregister.chw.util;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.core.utils.ChwServiceSchedule;
import org.smartregister.domain.AlertStatus;

public class ChwServiceScheduleTest extends BaseUnitTest {

    @Mock
    private ChwServiceSchedule chwServiceSchedule;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chwServiceSchedule = new ChwServiceSchedule();
    }

    @Test
    public void calculateAlertStatusNormalExpiryDateSame() throws Exception {
        DateTime dueDateTime = new DateTime();
        DateTime expiryDateTime = new DateTime();
        AlertStatus alertStatus = ReflectionHelpers.callInstanceMethod(chwServiceSchedule, "calculateAlertStatus",
                ReflectionHelpers.ClassParameter.from(DateTime.class, dueDateTime),
                ReflectionHelpers.ClassParameter.from(DateTime.class, expiryDateTime)
        );
        Assert.assertEquals("normal", alertStatus.value());
    }

    @Test
    public void calculateAlertStatusExpiredExpiryDateTesterday() throws Exception {
        DateTime dueDateTime = new DateTime();
        DateTime expiryDateTime = new DateTime(1556880664000L);
        AlertStatus alertStatus = ReflectionHelpers.callInstanceMethod(chwServiceSchedule, "calculateAlertStatus",
                ReflectionHelpers.ClassParameter.from(DateTime.class, dueDateTime),
                ReflectionHelpers.ClassParameter.from(DateTime.class, expiryDateTime)
        );
        Assert.assertEquals("expired", alertStatus.value());
    }
}
