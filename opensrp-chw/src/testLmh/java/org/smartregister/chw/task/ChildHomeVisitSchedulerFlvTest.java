package org.smartregister.chw.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.HomeAlertRule;
import org.smartregister.chw.dao.ChwChildDao;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChwChildDao.class)
public class ChildHomeVisitSchedulerFlvTest {
    private ChildHomeVisitSchedulerFlv childHomeVisitSchedulerFlv;

    @Before
    public void setUp(){
        childHomeVisitSchedulerFlv = Mockito.spy(ChildHomeVisitSchedulerFlv.class);
    }

    @Test
    public void testGenerateTasks() throws Exception {
        PowerMockito.spy(ChwChildDao.class);
        PowerMockito.doReturn(true)
                .when(ChwChildDao.class, "hasDueVaccines", "6b707d50-38e1-4a7a-a207-1d063d135cca");
        BaseScheduleTask baseScheduleTask = new BaseScheduleTask();
        HomeAlertRule alertRule = Mockito.mock(HomeAlertRule.class);
        Mockito.when(alertRule.getOverDueDate()).thenReturn(new Date());
        Mockito.doReturn(alertRule)
                .when(childHomeVisitSchedulerFlv)
                .getAlertRule(eq("6b707d50-38e1-4a7a-a207-1d063d135cca"));

        List<ScheduleTask> result = childHomeVisitSchedulerFlv.generateTasks("6b707d50-38e1-4a7a-a207-1d063d135cca", "testing", new Date(), baseScheduleTask);
        Assert.assertEquals(baseScheduleTask, result.get(0));
    }
}