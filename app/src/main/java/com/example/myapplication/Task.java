package com.example.myapplication;

public class Task {
    private String category;
    private String title;
    private String time;
    private String status;

    public Task(String category, String title, String time, String status) {
        this.category = category;
        this.title = title;
        this.time = time;
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
