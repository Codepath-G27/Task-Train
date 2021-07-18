package com.eliasfang.calendify.models;

public class NotificationData {
    String title;
    String message;
    String alarm_id;


    public NotificationData(String title, String message) {
        this.title = title;
        this.message = message;
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

}
