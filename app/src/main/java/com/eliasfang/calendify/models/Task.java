package com.eliasfang.calendify.models;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eliasfang.calendify.alarmSetup.AlarmReceiver;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.eliasfang.calendify.fragments.TaskCreateDialogFragment.TAG;

@Entity(tableName = "task_table")
public class Task implements Parcelable{

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

    @ColumnInfo(name = "isExpanded")
    private boolean isExpanded;


    @ColumnInfo(name = "alarmId")
    private Integer alarmId;


    @ColumnInfo(name = "monday")
    private boolean monday;

    @ColumnInfo(name = "tuesday")
    private boolean tuesday;

    @ColumnInfo(name = "wed")
    private boolean wed;

    @ColumnInfo(name = "thur")
    private boolean thur;

    @ColumnInfo(name = "fri")
    private boolean fri;

    @ColumnInfo(name = "sat")
    private boolean sat;

    @ColumnInfo(name = "sun")
    private boolean sun;

    @ColumnInfo(name = "notificationTime")
    private String notificationTime;

    @ColumnInfo(name = "hasAlarmBuddy")
    private boolean hasAlarmBuddy;

    @ColumnInfo(name = "timezone")
    private String timezone;


    public Task(String name, String description, String eventDate, String eventTime, boolean isCompleted, boolean hasAlarm, String category, String location, boolean isExpanded) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.eventTime = eventTime;
        this.isCompleted = isCompleted;
        this.hasAlarm = hasAlarm;
        this.category = category;
        this.hasAlarmBuddy = false;
        this.isExpanded = isExpanded;
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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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

    public boolean isHasAlarmBuddy() {
        return hasAlarmBuddy;
    }

    public void setHasAlarmBuddy(boolean hasAlarmBuddy) {
        this.hasAlarmBuddy = hasAlarmBuddy;
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

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.hasAlarm = false;
    }


    public void setAlarm(Context context, Activity activity) {

        //TODO: HANDLE DIFFERENT TIMEZONES LIKE 12 hour differences for sharing alarms with friends
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
        intent.putExtra("THURSDAY", this.thur);
        intent.putExtra("FRIDAY", this.fri);
        intent.putExtra("SATURDAY", this.sat);
        intent.putExtra("SUNDAY", this.sun);
        intent.putExtra("alarmBuddy", this.hasAlarmBuddy);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        String TimeandDate = date + " " + this.notificationTime;
        DateFormat formatter = new SimpleDateFormat("M-d-yyyy hh:mm");
        Log.i("AlarmBuddyActivity", "TIme and date: " + TimeandDate);
        Log.i("AlarmBuddyActivity", "Date: " + this.getEventDate() + " Time: " + this.getNotificationTime());


        if (!recur) {
            try {


                Date date1 = formatter.parse(TimeandDate);
                TimeZone timeZone = TimeZone.getTimeZone(this.timezone);
                Log.i(TAG, "TZ: " + this.timezone);
                Calendar ret = new GregorianCalendar(timeZone);
                ret.setTimeInMillis(date1.getTime());
                int offset = timeZone.getOffset(date1.getTime()) - TimeZone.getDefault().getOffset(date1.getTime());
                Log.i(TAG, "Offset: " + offset);

                ret.add(Calendar.MILLISECOND, -offset);


                Log.i(TAG, "Old timezone offset: " + timeZone.getOffset(date1.getTime()) + " New timezone offset: " + TimeZone.getDefault().getOffset(date1.getTime()));
                Log.i(TAG, "REf time " + ret.getTimeInMillis());
                Log.i(TAG, "REf time " + ret.getTime());
                am.setExact(AlarmManager.RTC_WAKEUP, ret.getTimeInMillis(), pendingIntent);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "It is recurring");
            try {

                Date date1 = formatter.parse(TimeandDate);
                TimeZone timeZone = TimeZone.getDefault();
                Calendar ret = new GregorianCalendar(timeZone);
                ret.setTimeInMillis(date1.getTime() +
                        timeZone.getOffset(date1.getTime()) -
                        TimeZone.getDefault().getOffset(date1.getTime()));
                Log.i(TAG, "Old timezone offset: " + timeZone.getOffset(date1.getTime()) + "New timezone offset: " + TimeZone.getDefault().getOffset(date1.getTime()));
                Log.i(TAG, "REf time " + ret.getTimeInMillis());
                Log.i(TAG, "REf time " + ret.getTime());
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, ret.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", hasAlarm=" + hasAlarm +
                ", eventDate='" + eventDate + '\'' +
                ", location='" + location + '\'' +
                ", recurrence=" + recurrence +
                ", eventTime='" + eventTime + '\'' +
                ", category='" + category + '\'' +
                ", isExpanded=" + isExpanded +
                ", alarmId=" + alarmId +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wed=" + wed +
                ", thur=" + thur +
                ", fri=" + fri +
                ", sat=" + sat +
                ", sun=" + sun +
                ", notificationTime='" + notificationTime + '\'' +
                '}';
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
}
