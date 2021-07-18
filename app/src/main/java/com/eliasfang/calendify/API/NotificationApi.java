package com.eliasfang.calendify.API;

import com.eliasfang.calendify.models.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.eliasfang.calendify.models.Constants.CONTENT_TYPE;
import static com.eliasfang.calendify.models.Constants.SERVER_KEY;

public interface NotificationApi {

    @Headers({"Authorization: key="+ SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotification> postNotification(@Body PushNotification notification);
}
