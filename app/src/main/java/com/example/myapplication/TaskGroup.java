package com.example.myapplication;

public class TaskGroup {
    private String groupName;
    private String taskCount;
    private String percent;

    public TaskGroup(String groupName, String taskCount, String percent) {
        this.groupName = groupName;
        this.taskCount = taskCount;
        this.percent = percent;
    }

    public String getGroupName() { return groupName; }
    public String getTaskCount() { return taskCount; }
    public String getPercent() { return percent; }
}