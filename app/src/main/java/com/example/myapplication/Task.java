package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

public class Task {
    private String id;
    private String category;
    private String title;
    private String content;
    private int progress;
    private String status;
    private String startDate;
    private String endDate;
    private Map<String, Subtask> subtasks;

    public Task() {
        this.subtasks = new HashMap<>();
    }

    public Task(String category, String title, String content, int progress, String status, String startDate, String endDate) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.progress = progress;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subtasks = new HashMap<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Map<String, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Map<String, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
