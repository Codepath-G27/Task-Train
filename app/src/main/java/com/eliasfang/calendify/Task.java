package com.eliasfang.calendify;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "date")
    private String date; //TODO need to change this to some appropriate Java library
    @ColumnInfo(name = "time")
    private String time; //TODO same with this one
    @ColumnInfo(name = "status")
    private boolean status;
    @ColumnInfo(name = "hasAlarm")
    private boolean hasAlarm;
    @ColumnInfo(name = "minutesBefore")
    private int minutesBefore;

    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "recurrence")
    private String recurrence; //TODO also change this to some appropriate Java time library

    public Task(String name, String description, String date, String time, boolean status, boolean hasAlarm, int minutesBefore) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = status;
        this.hasAlarm = hasAlarm;
        this.minutesBefore = minutesBefore;
    }

    @Ignore
    public Task(String name, String description, String date, String time, boolean status, boolean hasAlarm, int minutesBefore, String location, String recurrence) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = status;
        this.hasAlarm = hasAlarm;
        this.minutesBefore = minutesBefore;
        this.location = location;
        this.recurrence = recurrence;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public int getMinutesBefore() {
        return minutesBefore;
    }

    public void setMinutesBefore(int minutesBefore) {
        this.minutesBefore = minutesBefore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
}