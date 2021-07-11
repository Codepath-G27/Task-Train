package com.eliasfang.calendify.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eliasfang.calendify.alarmSetup.Alarm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @ColumnInfo(name = "eventDate")
    private String eventDate;

    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "recurrence")
    private String recurrence; //TODO also change this to some appropriate Java time library

    @ColumnInfo(name = "eventTime")
    private String eventTime;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name ="isExpanded")
    private boolean isExpanded;


    @ColumnInfo(name ="month")
    private Integer month;

    @ColumnInfo(name ="year")
    private Integer year;

    @ColumnInfo(name ="day")
    private Integer day;

    @ColumnInfo(name ="hour")
    private Integer hour;

    @ColumnInfo(name ="minute")
    private Integer minute;


    @ColumnInfo(name = "alarmId")
    private Integer alarmId;




    public Task(String name, String description, String eventDate, String eventTime, long epochTime, boolean isCompleted, boolean hasAlarm, int minutesBefore, String category, String location, boolean isExpanded) {
        this.name = name;
        this.description = description;
        this.epochTime = epochTime;
        this.eventDate = eventDate;
        this.location = location;
        this.eventTime = eventTime;
        this.isCompleted = isCompleted;
        this.hasAlarm = hasAlarm;
        this.minutesBefore = minutesBefore;
        this.category = category;
    }



    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
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

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventtime) {
        this.eventTime = eventtime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }






    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }


    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
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
        eventDate = in.readString();
        eventTime = in.readString();
        category = in.readString();
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
        dest.writeString(category);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
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


    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.hasAlarm = false;

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId);
        //Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        Log.i("cancel", toastText);
    }


}
