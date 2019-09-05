package org.smartregister.chw.core.domain;

import org.smartregister.chw.core.contract.ScheduleTask;

import java.util.Date;

public class BaseScheduleTask implements ScheduleTask {
    private String ID;
    private String baseEntityID;
    private String scheduleGroupName;
    private String scheduleName;
    private Date scheduleDueDate;
    private Date scheduleNotDoneDate;
    private Date scheduleOverDueDate;
    private Date scheduleExpiryDate;
    private Date scheduleCompletionDate;
    private Date updatedAt;
    private Date createdAt;

    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }

    @Override
    public String getScheduleGroupName() {
        return scheduleGroupName;
    }

    public void setScheduleGroupName(String scheduleGroupName) {
        this.scheduleGroupName = scheduleGroupName;
    }

    @Override
    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    @Override
    public Date getScheduleDueDate() {
        return scheduleDueDate;
    }

    public void setScheduleDueDate(Date scheduleDueDate) {
        this.scheduleDueDate = scheduleDueDate;
    }

    @Override
    public Date getScheduleNotDoneDate() {
        return scheduleNotDoneDate;
    }

    public void setScheduleNotDoneDate(Date scheduleNotDoneDate) {
        this.scheduleNotDoneDate = scheduleNotDoneDate;
    }

    @Override
    public Date getScheduleOverDueDate() {
        return scheduleOverDueDate;
    }

    public void setScheduleOverDueDate(Date scheduleOverDueDate) {
        this.scheduleOverDueDate = scheduleOverDueDate;
    }

    @Override
    public Date getScheduleExpiryDate() {
        return scheduleExpiryDate;
    }

    public void setScheduleExpiryDate(Date scheduleExpiryDate) {
        this.scheduleExpiryDate = scheduleExpiryDate;
    }

    @Override
    public Date getScheduleCompletionDate() {
        return scheduleCompletionDate;
    }

    public void setScheduleCompletionDate(Date scheduleCompletionDate) {
        this.scheduleCompletionDate = scheduleCompletionDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
