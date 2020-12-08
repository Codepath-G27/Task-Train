package com.eliasfang.calendify;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "epochTime")
    private long epochTime; //TODO same with this one
    @ColumnInfo(name = "isCompleted")
    private boolean isCompleted;
    @ColumnInfo(name = "hasAlarm")
    private boolean hasAlarm;
    @ColumnInfo(name = "minutesBefore")
    private int minutesBefore;

    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "recurrence")
    private String recurrence; //TODO also change this to some appropriate Java time library

    public Task(String name, String description, long epochTime, boolean isCompleted, boolean hasAlarm, int minutesBefore) {
        this.name = name;
        this.description = description;
        this.epochTime = epochTime;
        this.isCompleted = isCompleted;
        this.hasAlarm = hasAlarm;
        this.minutesBefore = minutesBefore;
    }

    @Ignore
    public Task(String name, String description, long epochTime, boolean isCompleted, boolean hasAlarm, int minutesBefore, String location, String recurrence) {
        this.name = name;
        this.description = description;
        this.epochTime = epochTime;
        this.isCompleted = isCompleted;
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

    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
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


    protected Task(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        epochTime = in.readLong();
        isCompleted = in.readByte() != 0x00;
        hasAlarm = in.readByte() != 0x00;
        minutesBefore = in.readInt();
        location = in.readString();
        recurrence = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(epochTime);
        dest.writeByte((byte) (isCompleted ? 0x01 : 0x00));
        dest.writeByte((byte) (hasAlarm ? 0x01 : 0x00));
        dest.writeInt(minutesBefore);
        dest.writeString(location);
        dest.writeString(recurrence);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
