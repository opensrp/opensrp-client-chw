package org.smartregister.chw.core.contract;

import java.util.Date;

public interface ScheduleTask {

    String getID();

    String getBaseEntityID();

    String getScheduleGroupName();

    String getScheduleName();

    Date getScheduleDueDate();

    Date getScheduleNotDoneDate();

    Date getScheduleOverDueDate();

    Date getScheduleExpiryDate();

    Date getScheduleCompletionDate();

}
