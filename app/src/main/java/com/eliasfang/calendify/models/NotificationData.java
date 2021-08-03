package com.eliasfang.calendify.models;

public class NotificationData {
    String title;
    String message;
    String alarm_id;
    String senderUid;


    public NotificationData(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public NotificationData(String title, String message, String senderUid, String alarm_id) {
        this.title = title;
        this.message = message;
        this.senderUid = senderUid;
        this.alarm_id = alarm_id;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderUid(String senderUid) { this.senderUid = senderUid; }
}
