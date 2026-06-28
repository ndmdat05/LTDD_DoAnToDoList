package com.example.myapplication;

public class Task {
    private String category;
    private String title;
    private int progress;
    private String status;

    public Task() {
    }

    public Task(String category, String title, int progress, String status) {
        this.category = category;
        this.title = title;
        this.progress = progress;
        this.status = status;
    }

    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public int getProgress() { return progress; }
    public String getStatus() { return status; }
}