package org.smartregister.chw.core.utils;

import org.json.JSONObject;

public class ServiceTask {
    private String taskTitle;
    private String taskLabel;
    private boolean isGreen;
    private String taskType;
    private JSONObject taskJson;

    public JSONObject getTaskJson() {
        return taskJson;
    }

    public void setTaskJson(JSONObject taskJson) {
        this.taskJson = taskJson;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public void setGreen(boolean green) {
        isGreen = green;
    }

}
