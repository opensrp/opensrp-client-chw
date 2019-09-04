package org.smartregister.chw.core.contract;

import java.util.Date;

public interface ScheduleTask {

    String getBaseEntityID();

    String getScheduleGroupName();

    String getScheduleName();

    Date getScheduleDueDate();

    Date getScheduleOverDueDate();

    Date getScheduleExpiryDate();

    Date getScheduleCompletionDate();

}
