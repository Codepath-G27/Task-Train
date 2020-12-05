package com.eliasfang.calendify.alarmSetup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eliasfang.calendify.MainActivity;

public class Reciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
