package com.example.myapplication;

public class Task {
    private String category;
    private String title;
    private String content;
    private int progress;
    private String status;
    private String startDate;
    private String endDate;

    public Task() {
    }
    public Task(String category, String title, String content, int progress, String status, String startDate, String endDate) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.progress = progress;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getProgress() { return progress; }
    public String getStatus() { return status; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
}