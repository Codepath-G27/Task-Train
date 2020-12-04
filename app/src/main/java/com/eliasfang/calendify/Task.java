package com.eliasfang.calendify;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
}
