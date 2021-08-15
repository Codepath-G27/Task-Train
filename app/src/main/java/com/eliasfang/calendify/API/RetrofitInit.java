package com.eliasfang.calendify.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.eliasfang.calendify.models.Constants.BASE_URL;

public class RetrofitInit {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }



}
