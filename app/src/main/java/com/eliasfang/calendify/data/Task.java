package com.eliasfang.calendify.data;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eliasfang.calendify.alarmSetup.AlarmReceiver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @ColumnInfo(name = "isCompleted")
    private boolean isCompleted;

    @ColumnInfo(name = "hasAlarm")
    private boolean hasAlarm;

    @ColumnInfo(name = "eventDate")
    private String eventDate;

    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "recurrence")
    private Boolean recurrence;

    @ColumnInfo(name = "eventTime")
    private String eventTime;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name ="isExpanded")
    private boolean isExpanded;


    @ColumnInfo(name = "alarmId")
    private Integer alarmId;


    @ColumnInfo(name ="monday")
    private boolean monday;

    @ColumnInfo(name ="tuesday")
    private boolean tuesday;

    @ColumnInfo(name ="wed")
    private boolean wed;

    @ColumnInfo(name ="thur")
    private boolean thur;

    @ColumnInfo(name ="fri")
    private boolean fri;

    @ColumnInfo(name ="sat")
    private boolean sat;

    @ColumnInfo(name ="sun")
    private boolean sun;

    @ColumnInfo(name ="notificationTime")
    private String notificationTime;



    public Task(String name, String description, String eventDate, String eventTime, boolean isCompleted, boolean hasAlarm, String category, String location, boolean isExpanded) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.eventTime = eventTime;
        this.isCompleted = isCompleted;
        this.hasAlarm = hasAlarm;
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


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Boolean recurrence) {
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



    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWed() {
        return wed;
    }

    public void setWed(Boolean wed) {
        this.wed = wed;
    }

    public Boolean getThur() {
        return thur;
    }

    public void setThur(Boolean thur) {
        this.thur = thur;
    }

    public Boolean getFri() {
        return fri;
    }

    public void setFri(Boolean fri) {
        this.fri = fri;
    }

    public Boolean getSat() {
        return sat;
    }

    public void setSat(Boolean sat) {
        this.sat = sat;
    }

    public Boolean getSun() {
        return sun;
    }

    public void setSun(Boolean sun) {
        this.sun = sun;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String time) {
        this.notificationTime = time;
    }


    public void setDays(Boolean mon, Boolean tues, Boolean wed, Boolean thur, Boolean fri, Boolean sat, Boolean sun) {
        this.monday = mon;
        this.tuesday = tues;
        this.wed = wed;
        this.thur = thur;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        isCompleted = in.readByte() != 0x00;
        hasAlarm = in.readByte() != 0x00;
        location = in.readString();
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
        dest.writeByte((byte) (isCompleted ? 0x01 : 0x00));
        dest.writeByte((byte) (hasAlarm ? 0x01 : 0x00));
        dest.writeString(location);
        dest.writeString(category);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
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
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.hasAlarm = false;
    }


    public void setAlarm(Context context, Activity activity) {
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        String text = this.name;
        String date = this.eventDate;
        String time = this.eventTime;
        int id = this.alarmId;
        Boolean recur = this.recurrence;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("event", text);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("id", id);
        intent.putExtra("RECURRING", recur);
        intent.putExtra("MONDAY", this.monday);
        intent.putExtra("TUESDAY", this.tuesday);
        intent.putExtra("WEDNESDAY", this.wed);
        intent.putExtra("THURSDAY", this.tuesday);
        intent.putExtra("FRIDAY", this.fri);
        intent.putExtra("SATURDAY", this.sat);
        intent.putExtra("SUNDAY", this.sun);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        String TimeandDate = date + " " + this.notificationTime;
        DateFormat formatter = new SimpleDateFormat("M-d-yyyy hh:mm");
        
        if(!recur) {
            try {
                Date date1 = formatter.parse(TimeandDate);
                am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Date date1 = formatter.parse(TimeandDate);
                am.setRepeating(
                        AlarmManager.RTC_WAKEUP, date1.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


}
