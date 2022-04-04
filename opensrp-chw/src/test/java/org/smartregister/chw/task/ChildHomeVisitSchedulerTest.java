package org.smartregister.chw.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.BaseUnitTest;
import org.junit.Assert;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class ChildHomeVisitSchedulerTest extends BaseUnitTest {

    private ChildHomeVisitScheduler childHomeVisitScheduler;
    private ChildHomeVisitScheduler.Flavor flavor;

    @Before
    public void setUp() {
        childHomeVisitScheduler = Mockito.spy(ChildHomeVisitScheduler.class);
        flavor = Mockito.mock(ChildHomeVisitScheduler.Flavor.class);
        Whitebox.setInternalState(childHomeVisitScheduler, "flavor", flavor);
    }

    @Test
    public void testGenerateTasks() {
        childHomeVisitScheduler.generateTasks("6b707d50-38e1-4a7a-a207-1d063d135cca", "testing", new Date());
        Mockito.verify(flavor)
                .generateTasks(eq("6b707d50-38e1-4a7a-a207-1d063d135cca"), eq("testing"), any(Date.class), any(BaseScheduleTask.class));
    }

    @Test
    public void testGetScheduleName() {
        Assert.assertEquals(CoreConstants.SCHEDULE_TYPES.CHILD_VISIT, childHomeVisitScheduler.getScheduleName());
    }

    @Test
    public void testGetScheduleGroup() {
        Assert.assertEquals(CoreConstants.SCHEDULE_GROUPS.HOME_VISIT, childHomeVisitScheduler.getScheduleGroup());
    }

    @Test
    public void testToScheduleList() {
        ScheduleTask scheduleTask = Mockito.mock(ScheduleTask.class);
        ScheduleTask scheduleTask1 = Mockito.mock(ScheduleTask.class);

        List<ScheduleTask> result = childHomeVisitScheduler.toScheduleList(scheduleTask, scheduleTask1);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(scheduleTask, result.get(0));
        Assert.assertEquals(scheduleTask1, result.get(1));
    }
}