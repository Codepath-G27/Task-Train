package com.eliasfang.calendify.models;

import com.eliasfang.calendify.models.NotificationData;

public class PushNotification {
    NotificationData data;
    String to;

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }
}
