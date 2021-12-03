package org.smartregister.chw.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.smartregister.chw.core.rule.HomeAlertRule;

import static org.mockito.ArgumentMatchers.eq;

public class DefaultChildHomeVisitSchedulerFlvTest {

    private DefaultChildHomeVisitSchedulerFlv childHomeVisitSchedulerFlv;

    @Before
    public void setUp(){
        childHomeVisitSchedulerFlv = Mockito.spy(DefaultChildHomeVisitSchedulerFlv.class);
    }

    @Test
    public void testGenerateTasks() {
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