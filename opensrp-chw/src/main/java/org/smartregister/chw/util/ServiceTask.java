package org.smartregister.chw.util;

public class ServiceTask {
    private String taskTitle;
    private String taskLabel;
    private boolean isGreen;
    private String taskType;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setGreen(boolean green) {
        isGreen = green;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskLabel() {
        return taskLabel;
    }

    public void setTaskLabel(String taskLabel) {
        this.taskLabel = taskLabel;
    }

    public boolean isGreen() {
        return isGreen;
    }

}
