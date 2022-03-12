package com.example.todolist;

import java.util.ArrayList;

public class Task {

    private String title, description, date, time, priority, calenderIcon, tags, tagIcon;
    private StringBuilder tagsList;
    private boolean done;

    public Task(String title, String description, String date, String time, String priority, String calenderIcon, String tags, String tagIcon, boolean done) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.calenderIcon = calenderIcon;
        this.tags = tags;
        this.tagIcon = tagIcon;
        this.done = done;

        StringBuilder tagsList = new StringBuilder();
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
    public String getTime() {
        return time;
    }

    public String getPriority() {
        return priority;
    }

    public String getCalender() {
        return calenderIcon;
    }

    public String getTags() {
        return tags;
    }

    public boolean isDone() {
        return this.done;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCalender(String calender) {
        this.calenderIcon = calender;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public StringBuilder getTagsList() {
        return tagsList;
    }

    public void setTagsList(StringBuilder tagsList) {
        this.tagsList = tagsList;
    }
}
