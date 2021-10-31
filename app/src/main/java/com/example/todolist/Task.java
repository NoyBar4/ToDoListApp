package com.example.todolist;

public class Task {

    private String title, description, date, priority, calender;
    private boolean done;

    public Task(String title, String description, String date, String priority, String calender, boolean done) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.calender = calender;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getPriority() {
        return priority;
    }

    public String getCalender() {
        return calender;
    }

    public boolean isDone() {
        return done;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCalender(String calender) {
        this.calender = calender;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
