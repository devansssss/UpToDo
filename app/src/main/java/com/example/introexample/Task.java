package com.example.introexample;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable, Comparable<Task>{
    private String title;
    private String description;
    private Date dueDate;
    private String dueTime;
    private String UID;
    private int Priority;

    public Task() {

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        this.Priority = priority;
    }

    public Task(String title, String description, Date dueDate, String dueTime, String UID, int priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.UID = UID;
        this.Priority = priority;
    }




    @Override
    public int compareTo(Task o) {
        int dateComparison = this.dueDate.compareTo(o.dueDate);
        if (dateComparison==0){
            return this.dueTime.compareTo(o.dueTime);
        }else {
            return dateComparison;
        }
    }
}
